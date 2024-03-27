package com.api.stepdefinition;

import static org.junit.Assert.*;

import com.api.model.LocationAdd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.api.utils.JsonReader;
import com.api.utils.ResponseHandler;
import com.api.utils.TestContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateBookingStepdefinition {
	private TestContext context;
	private static final Logger LOG = LogManager.getLogger(CreateBookingStepdefinition.class);

	public CreateBookingStepdefinition(TestContext context) {
		this.context = context;
	}


	@When("user creates a new place using data {string} from JSON file {string}")
	public void userCreatesANewPlaceUsingDataFromJSONFile(String dataKey, String JSONFile) {

		context.response = context.requestSetup().body(JsonReader.getRequestBody(JSONFile,dataKey))
				.when().post(context.session.get("endpoint").toString());

		LocationAdd locationAdd = ResponseHandler.deserializedResponse(context.response, LocationAdd.class);
		assertNotNull("Booking not created", locationAdd);
		LOG.info("Newly created booking ID: "+locationAdd.getPlace_id());
		context.session.put("Place_id", locationAdd.getPlace_id());
	}

	@And("user updates the place details using data {string} from JSON file {string}")
	public void userUpdatesThePlaceDetailsUsingDataFromJSONFile(String dataKey, String JSONFile) {
		context.response = context.requestSetup()
				.body(JsonReader.getRequestBody(JSONFile,dataKey).replace("{{place_id}}",context.session.get("Place_id").toString()))
				.when().put(context.session.get("endpoint").toString());

		//BookingDetailsDTO bookingDetailsDTO = ResponseHandler.deserializedResponse(context.response, BookingDetailsDTO.class);
		assertNotNull("Booking not created", context.response);
	}

	@When("user makes a request to view place_id")
	public void userMakesARequestToViewPlace_id() {
		context.response = context.requestSetup().when().get(context.session.get("endpoint").toString());
		assertNotNull("Booking ID not found!", context.response);
	}

	@When("user makes a request to delete place")
	public void userMakesARequestToDeletePlace() {
		String reqBody = "{\n" +
				"    \"place_id\": \""+context.session.get("Place_id").toString()+"\"\n" +
				"}";
		context.response = context.requestSetup()
				.when().body(reqBody).post(context.session.get("endpoint").toString());
	}

	@Given("user has access to endpoint {string}")
	public void userHasAccessToEndpoint(String endpoint) {
		context.session.put("endpoint", endpoint);
	}



	@Then("user should get the response code {int}")
	public void userShpuldGetTheResponseCode(Integer statusCode) {
		assertEquals(Long.valueOf(statusCode), Long.valueOf(context.response.getStatusCode()));
	}
}
