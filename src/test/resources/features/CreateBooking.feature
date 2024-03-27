@createPlace
Feature: To create update delete place using google api's

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
    When user makes a request to view place_id
    When user makes a request to delete place
    Then user should get the response code 200

    Examples:
      | dataKey        | JSONFile         |
      | createlocation1 | mapsBody.json    |
