Feature: 회원가입

  Background:
    * url baseUrl

  Scenario: 구매자 회원가입
    Given path '/api/v1/sign-up/local'
    And request
      """
      {
        "email": "buyer@example.com",
        "password": "Password1!",
        "nickname": "구매자"
      }
      """
    When method POST
    Then status 201
    And match response.userId == '#number'
