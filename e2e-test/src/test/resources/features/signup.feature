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
    And match response.accessToken == '#string'
    * def userId = response.userId
    * def accessToken = response.accessToken

  Scenario: 회원 활성화 및 회원가입 실패 처리
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
    * def userId = response.userId
    * def accessToken = response.accessToken

    Given path '/api/v1/users', userId, 'activate'
    And header Authorization = 'Bearer ' + accessToken
    When method POST
    Then status 204

    Given path '/api/v1/users', userId, 'signup-failed'
    And header Authorization = 'Bearer ' + accessToken
    When method POST
    Then status 204
