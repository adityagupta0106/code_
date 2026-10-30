package com.serviceplus.tracking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;

/** One owner for public verification state. String serialization is required by the Lua scripts. */
@Service
public class PublicTrackingAuthorizationService {
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final SecureRandom random = new SecureRandom();
    @Value("${tracking.public.enabled:false}") private boolean enabled;
    @Value("${tracking.public.proof-ttl-seconds:180}") private long proofTtl;
    @Value("${tracking.public.request-ttl-seconds:300}") private long requestTtl;
    @Value("${tracking.public.requests-per-minute:10}") private int requestLimit;

    public PublicTrackingAuthorizationService(StringRedisTemplate redis, ObjectMapper mapper) {
        this.redis=redis;
        this.mapper=mapper.copy().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,true);
    }
    public void requireEnabled() {
        if (!enabled) throw new SPRuntimeError("Public tracking verification is unavailable",HttpStatus.SERVICE_UNAVAILABLE);
    }
    public void rateLimit(String tenant, int service, String peer) {
        requireEnabled();
        String key="tracking:public:rate:"+fingerprint(List.of(tenant,service,Objects.toString(peer,"unknown")));
        Long count=redis.execute(new DefaultRedisScript<>(
            "local n=redis.call('INCR',KEYS[1]); if n==1 then redis.call('EXPIRE',KEYS[1],60) end; return n",Long.class),List.of(key));
        if(count==null || count>requestLimit) throw new SPRuntimeError("Verification request limit exceeded",HttpStatus.TOO_MANY_REQUESTS);
    }
    public String fingerprint(Object value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(mapper.writeValueAsBytes(value))); }
        catch(Exception ex) { throw new IllegalStateException("Verification state unavailable"); }
    }
    public String randomId() {
        byte[] bytes=new byte[32];random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public String saveRequest(Map<String,Object> state) { return save("request",state,requestTtl); }
    public Map<String,Object> request(String id) { return read("request",id); }
    public boolean consumeRequest(String id, Map<String,Object> state) {
        return compareDelete("request",id,state);
    }
    public String issue(String tenant,int service,String application,String method,String configuration) {
        requireEnabled();
        return save("proof",Map.of("tenantId",tenant,"serviceId",service,"applicationId",application,
            "method",method,"purpose","PUBLIC_TRACKING","configuration",configuration),proofTtl);
    }
    public void consume(String proof,String tenant,int service,String application,String configuration) {
        requireEnabled();
        Map<String,Object> scope=read("proof",proof);
        if(!tenant.equals(scope.get("tenantId")) || !Integer.valueOf(service).equals(scope.get("serviceId"))
            || !application.equals(scope.get("applicationId")) || !configuration.equals(scope.get("configuration"))
            || !"PUBLIC_TRACKING".equals(scope.get("purpose")) || !compareDelete("proof",proof,scope)) throw denied();
    }
    private String key(String kind,String id) {
        if(id==null || !id.matches("[A-Za-z0-9_-]{43}")) throw denied();
        return "tracking:public:"+kind+":"+fingerprint(id);
    }
    private String save(String kind,Map<String,Object> value,long ttl) {
        if(ttl<1 || ttl>900) throw new IllegalStateException("Invalid public verification TTL");
        String id=randomId();
        redis.opsForValue().set(key(kind,id),json(value),Duration.ofSeconds(ttl));return id;
    }
    private Map<String,Object> read(String kind,String id) {
        String data=redis.opsForValue().get(key(kind,id));if(data==null) throw denied();
        try {return mapper.readValue(data,new TypeReference<Map<String,Object>>(){});}
        catch(Exception ex) {throw denied();}
    }
    private boolean compareDelete(String kind,String id,Map<String,Object> value) {
        Long result=redis.execute(new DefaultRedisScript<>(
            "if redis.call('GET',KEYS[1])==ARGV[1] then return redis.call('DEL',KEYS[1]) end; return 0",Long.class),
            List.of(key(kind,id)),json(value));return Long.valueOf(1).equals(result);
    }
    private String json(Object value) {
        try{return mapper.writeValueAsString(value);}catch(Exception ex){throw new IllegalStateException("Verification state unavailable");}
    }
    public static SPRuntimeError denied() {return new SPRuntimeError("Verification required or expired",HttpStatus.FORBIDDEN);}
}
