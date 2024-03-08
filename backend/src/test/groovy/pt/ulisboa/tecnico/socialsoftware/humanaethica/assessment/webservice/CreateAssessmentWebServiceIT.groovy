package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.InstitutionService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.ThemeService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAssessmentWebServiceIT extends SpockTest {
    
    @LocalServerPort
    private int port
    
    def assessmentDto
    def institutionId
    def institutionDto
    def activityId
    def institution

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        

        given: 'activity info'
        def activityDto = createActivityDto(ACTIVITY_NAME_1, ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,THREE_DAYS_AGO,TWO_DAYS_AGO,ONE_DAY_AGO, null)

        and: 'theme'
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1,Theme.State.APPROVED, null))

        and: 'institution'
        institution = institutionService.getDemoInstitution()

        and: 'activity'

        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)

        assessmentDto = createAssessmentDto(REVIEW_1, NOW)
    }

    def "login as volunteer, and create an assessment"() {
        given:
        def volunteer = demoVolunteerLogin()

        when: 'the volunteer tries to create an assessment'
        def response = webClient.post()
                .uri('/assessments/create/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: 'check response status' 
        response.review == REVIEW_1
        response.institution.id == institution.id
        response.volunteer.id == volunteer.id
        and: 'check database data'
        assessmentRepository.count() == 1
        def assessment = assessmentRepository.findAll().get(0)
        assessment.getReview() == REVIEW_1

        cleanup:
        deleteAll()

    }

    def "login as volunteer, and create an assessment with error"() {
        given:
        demoVolunteerLogin()
        and: 'a empty motivation'
        assessmentDto.review = " "

        when: 'the volunteer tries to create an assessment'
        webClient.post()
                .uri('/assessments/create/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: 'check response status' 
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST
        assessmentRepository.count() == 0

        cleanup:
        deleteAll()
    }

    def "login as member, and create an assessment"() {
        given: 'a member'
        demoMemberLogin()

        when: 'the member tries to create assessment'
        webClient.post()
                .uri('/assessments/create/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        assessmentRepository.count() == 0
    }

    def "login as admin, and create an assessment"() {
        given: 'a admin'
        demoAdminLogin()

        when: 'the admin tries to create assessment'
        webClient.post()
                .uri('/assessments/create/' + institution.getId())
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then: "an error is returned"
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.FORBIDDEN
        assessmentRepository.count() == 0
    }
    
    def cleanup() {
        deleteAll()
    }
    
}