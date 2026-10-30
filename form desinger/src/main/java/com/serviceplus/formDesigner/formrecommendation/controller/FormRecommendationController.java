package com.serviceplus.formDesigner.formrecommendation.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.formrecommendation.service.FormRecommendationService;
import com.serviceplus.formDesigner.util.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/recommend")
public class FormRecommendationController {

	@Autowired
	private FormRecommendationService formRecommendationService;

	@GetMapping("/forms")
	public ResponseEntity<?> getAllFormsOfService(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "formid") String formId) {

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				JSONObject responseJson = new JSONObject();
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
				responseJson.put("status", HttpStatus.BAD_REQUEST.value());
				responseJson.put("message", "Something went wrong");

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson.toString());
			}

			return formRecommendationService.getFormData(formId);

		} catch (SPRuntimeError ex) {
			throw ex;
		} catch (Exception ex) {
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}