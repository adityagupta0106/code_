package com.serviceplus.metadata.kafka;

import static com.serviceplus.metadata.utility.CommonUtil.stringToEntity;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.metadata.dto.UserCredentialUpdationDto;
import com.serviceplus.metadata.entity.ExternalSystemRegistration;
import com.serviceplus.metadata.entity.ExternalSystemUser;
import com.serviceplus.metadata.repository.IExternalSystemRegistrationRepository;
import com.serviceplus.metadata.repository.IExternalSystemUserRepository;

@Service
public class KafkaService {

    private static final Logger KafkaLogger = LogManager.getLogger("KafkaLogger");

    private IExternalSystemRegistrationRepository registrationRepo;
    private IExternalSystemUserRepository externalSystemUserRepo;
    
    public KafkaService(IExternalSystemRegistrationRepository registrationRepo,
			IExternalSystemUserRepository externalSystemUserRepo) {
		super();
		this.registrationRepo = registrationRepo;
		this.externalSystemUserRepo = externalSystemUserRepo;
	}

	@Transactional
    public void updateUserCredential(String key, String message) {

        try {

            KafkaLogger.info("Data received to save User with key {} and message {}", key, message);

            UserCredentialUpdationDto user =
                    (UserCredentialUpdationDto) stringToEntity(message, UserCredentialUpdationDto.class);

            KafkaLogger.info("Saving user with signNo {} for clientId {}",user.getSignNo(),user.getReferenceId());
            if(user.getReferenceId()!=null) {
            ExternalSystemRegistration registration =registrationRepo.findByClientId(user.getReferenceId())
                            .orElseThrow(() ->new RuntimeException("External System not found for clientId : "
                                            + user.getReferenceId()));

            ExternalSystemUser extUser = new ExternalSystemUser();
            extUser.setExternalSystemRegId(registration.getId());
            extUser.setUserId(user.getUserId()); 
            extUser.setSignNo(user.getSignNo());
            extUser.setClientId(user.getReferenceId());
            extUser.setCreatedOn(new Date());

            externalSystemUserRepo.save(extUser);

            KafkaLogger.info("User saved successfully with signNo: {} for clientId {}",user.getSignNo(),user.getReferenceId());
            }

        } catch (Exception e) {

            KafkaLogger.error("Unable to save external user credentials for key {}",key, e);

        }
    }
}
