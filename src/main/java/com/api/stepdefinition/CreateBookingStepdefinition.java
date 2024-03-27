package com.api.stepdefinition;

import static org.junit.Assert.*;

import java.util.Map;

import com.api.model.BookingDetailsDTO;
import com.api.model.LocationAdd;
import io.cucumber.java.en.And;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.api.model.BookingDTO;
import com.api.utils.ExcelUtils;
import com.api.utils.JsonReader;
import com.api.utils.ResponseHandler;
import com.api.utils.TestContext;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;

public class CreateBookingStepdefinition {
	private TestContext context;
	private static final Logger LOG = LogManager.getLogger(CreateBookingStepdefinition.class);

	public CreateBookingStepdefinition(TestContext context) {
		this.context = context;
	}

	@When("user creates a booking")
	public void userCreatesABooking(DataTable dataTable) {
		Map<String,String> bookingData = dataTable.asMaps().get(0);
		JSONObject bookingBody = new JSONObject();
		bookingBody.put("firstname", bookingData.get("firstname"));
		bookingBody.put("lastname", bookingData.get("lastname"));
		bookingBody.put("totalprice", Integer.valueOf(bookingData.get("totalprice")));
		bookingBody.put("depositpaid", Boolean.valueOf(bookingData.get("depositpaid")));
		JSONObject bookingDates = new JSONObject();
		bookingDates.put("checkin", (bookingData.get("checkin")));
		bookingDates.put("checkout", (bookingData.get("checkout")));
		bookingBody.put("bookingdates", bookingDates);
		bookingBody.put("additionalneeds", bookingData.get("additionalneeds"));

		context.response = context.requestSetup().body(bookingBody.toString())
				.when().post(context.session.get("endpoint").toString());

		BookingDTO bookingDTO = ResponseHandler.deserializedResponse(context.response, BookingDTO.class);
		assertNotNull("Booking not created", bookingDTO);
		LOG.info("Newly created booking ID: "+bookingDTO.getBookingid());
		context.session.put("bookingID", bookingDTO.getBookingid());
		validateBookingData(new JSONObject(bookingData), bookingDTO);
	}

	private void validateBookingData(JSONObject bookingData, BookingDTO bookingDTO) {
		LOG.info(bookingData);
		assertNotNull("Booking ID missing", bookingDTO.getBookingid());
		assertEquals("First Name did not match", bookingData.get("firstname"), bookingDTO.getBooking().getFirstname());
		assertEquals("Last Name did not match", bookingData.get("lastname"), bookingDTO.getBooking().getLastname());
		assertEquals("Total Price did not match", bookingData.get("totalprice"), bookingDTO.getBooking().getTotalprice());
		assertEquals("Deposit Paid did not match", bookingData.get("depositpaid"), bookingDTO.getBooking().getDepositpaid());
		assertEquals("Additional Needs did not match", bookingData.get("additionalneeds"), bookingDTO.getBooking().getAdditionalneeds());
		assertEquals("Check in Date did not match", bookingData.get("checkin"), bookingDTO.getBooking().getBookingdates().getCheckin());
		assertEquals("Check out Date did not match", bookingData.get("checkout"), bookingDTO.getBooking().getBookingdates().getCheckout());
	}

	@When("user creates a booking using data {string} from Excel")
	public void userCreatesABookingUsingDataFromExcel(String dataKey) throws Exception {
		Map<String,String> excelDataMap = ExcelUtils.getData(dataKey);
		context.response = context.requestSetup().body(excelDataMap.get("requestBody"))
				.when().post(context.session.get("endpoint").toString());

		BookingDTO bookingDTO = ResponseHandler.deserializedResponse(context.response, BookingDTO.class);
		assertNotNull("Booking not created", bookingDTO);
		LOG.info("Newly created booking ID: "+bookingDTO.getBookingid());
		context.session.put("bookingID", bookingDTO.getBookingid());
		validateBookingData(new JSONObject(excelDataMap.get("responseBody")), bookingDTO);
		context.session.put("excelDataMap", excelDataMap);
	}

	@Then("user validates the response with JSON schema from Excel")
	public void userValidatesTheResponseWithJSONSchemaFromExcel() {
		context.response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(((Map<String,String>) context.session.get("excelDataMap")).get("responseSchema")));
		LOG.info("Successfully Validated schema from Excel");
	}

	@When("user creates a booking using data {string} from JSON file {string}")
	public void userCreatesABookingUsingDataFromJSONFile(String dataKey, String JSONFile) {
		context.response = context.requestSetup().body(JsonReader.getRequestBody(JSONFile,dataKey))
				.when().post(context.session.get("endpoint").toString());

		BookingDTO bookingDTO = ResponseHandler.deserializedResponse(context.response, BookingDTO.class);
		assertNotNull("Booking not created", bookingDTO);
		LOG.info("Newly created booking ID: "+bookingDTO.getBookingid());	
		context.session.put("bookingID", bookingDTO.getBookingid());
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
}
