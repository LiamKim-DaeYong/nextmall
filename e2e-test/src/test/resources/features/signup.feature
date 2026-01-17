Feature: 회원가입

  Background:
    * url baseUrl
    * def uuid = java.util.UUID.randomUUID().toString()

  Scenario: 구매자 회원가입
    * def email = 'buyer-' + uuid + '@example.com'
    Given path '/api/v1/sign-up/local'
    And request
      """
      {
        "email": "#(email)",
        "password": "Password1!",
        "nickname": "구매자"
      }
      """
    When method POST
    Then status 201
    And match response.userId == '#number'
