@bookerAPI @createBooking
Feature: To create a new booking in restful-booker

  @createBookingDataTable
  Scenario Outline: To create new booking using cucumber Data Table
    Given user has access to endpoint "/booking"
    When user creates a booking
      | firstname   | lastname   | totalprice   | depositpaid   | checkin   | checkout   | additionalneeds   |
      | <firstname> | <lastname> | <totalprice> | <depositpaid> | <checkin> | <checkout> | <additionalneeds> |
    Then user should get the response code 200
    And user validates the response with JSON schema "createBookingSchema.json"

    Examples: 
      | firstname | lastname | totalprice | depositpaid | checkin    | checkout   | additionalneeds |
      | John      | Doe      |       1200 | true        | 2021-05-05 | 2021-05-15 | Breakfast       |
      | Jane      | Doe      |       2400 | false       | 2021-06-01 | 2021-07-10 | Dinner          |

  @createBookingFromExcel
  Scenario Outline: To create new booking using Excel data
    Given user has access to endpoint "/booking"
    When user creates a booking using data "<dataKey>" from Excel
    Then user should get the response code 200
    And user validates the response with JSON schema from Excel

    Examples: 
      | dataKey        |
      | createBooking1 |
      | createBooking2 |

  @createPlace
  Scenario Outline: To create new place
    Given user has access to endpoint "/add/json"
    When user creates a new place using data "<dataKey>" from JSON file "<JSONFile>"
    Then user should get the response code 200

    Examples: 
      | dataKey        | JSONFile         |
      | createlocation1 | mapsBody.json    |

  @UpdatePlace
  Scenario Outline: To Update a existing place
    Given user has access to endpoint "/add/json"
    When user creates a new place using data "<dataKey>" from JSON file "<JSONFile>"
    Then user should get the response code 200
    Given user has access to endpoint "/update/json"
    And user updates the place details using data "<updatedataKey>" from JSON file "<JSONFile>"
    Then user should get the response code 200

    Examples:
      | dataKey        | JSONFile         |updatedataKey|
      | createlocation1 | mapsBody.json    |     updatelocation1       |

  @DeletePlace
  Scenario Outline: To Delete a place
    Given user has access to endpoint "/add/json"
    When user creates a new place using data "<dataKey>" from JSON file "<JSONFile>"
    Then user should get the response code 200
    Given user has access to endpoint "/get/json"
#    When user makes a request to view booking IDs
    When user makes a request to view place_id
#    When user makes a request to delete booking with basic auth "admin" & "password123"
    When user makes a request to delete place
    Then user should get the response code 200

    Examples:
      | dataKey        | JSONFile         |updatedataKey|
      | createlocation1 | mapsBody.json    |     updatelocation1       |
