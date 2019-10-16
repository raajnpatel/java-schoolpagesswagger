package com.lambdaschool.school.controller;

import com.lambdaschool.school.model.ErrorDetail;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.service.StudentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController
{
    @Autowired
    private StudentService studentService;

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    // Please note there is no way to add students to course yet!

    @ApiOperation(value = "list all students",
            response = Student.class,
            responseContainer = "List")
    @GetMapping(value = "/students",
            produces = {"application/json"})
    public ResponseEntity<?> listAllStudents(HttpServletRequest request)
    {
        logger.info(request.getMethod() + " " + request.getRequestURI() + " just accessed!");

        List<Student> myStudents = studentService.findAll();
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }

    @ApiOperation(value = "find a student using studentid",
            response = Student.class)
    @ApiResponses(value = {@ApiResponse(code = 200,
            message = "Student Found",
            response = Student.class), @ApiResponse(code = 404,
            message = "Student Not Found",
            response = ErrorDetail.class)})
    @GetMapping(value = "/student/{StudentId}",
            produces = {"application/json"})
    public ResponseEntity<?> getStudentById(
            @ApiParam(value = "Student Id",
                    required = true,
                    example = "1")
            @PathVariable
                    Long StudentId, HttpServletRequest request)
    {
        logger.info(request.getMethod() + " " + request.getRequestURI() + " just accessed! Parameter: " + StudentId);

        Student r = studentService.findStudentById(StudentId);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @ApiOperation(value = "find a student using a name",
            response = Student.class,
            responseContainer = "List")
    @GetMapping(value = "/student/namelike/{name}",
            produces = {"application/json"})
    public ResponseEntity<?> getStudentByNameContaining(
            @ApiParam(value = "Name or portion of a name",
                    required = true,
                    example = "allison, al")
            @PathVariable
                    String name, HttpServletRequest request)
    {
        logger.info(request.getMethod() + " " + request.getRequestURI() + " just accessed! Parameter: " + name);

        List<Student> myStudents = studentService.findStudentByNameLike(name);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }

    @ApiOperation(value = "add a new student")
    @PostMapping(value = "/student",
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseEntity<?> addNewStudent(@Valid
                                           @RequestBody
                                                   Student newStudent, HttpServletRequest request) throws URISyntaxException
    {
        logger.info(request.getMethod() + " " + request.getRequestURI() + " just accessed!");

        newStudent = studentService.save(newStudent);

        // set the location header for the newly created resource
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newStudentURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{Studentid}").buildAndExpand(newStudent.getStudid()).toUri();
        responseHeaders.setLocation(newStudentURI);

        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

    @ApiOperation(value = "update a student")
    @ApiResponses(value = {@ApiResponse(code = 200,
            message = "Student Updated"), @ApiResponse(code = 404,
            message = "Student Not Found",
            response = ErrorDetail.class)})
    @PutMapping(value = "/student/{Studentid}")
    public ResponseEntity<?> updateStudent(
            @RequestBody
                    Student updateStudent,
            @ApiParam(value = "Student Id",
                    required = true,
                    example = "1")
            @PathVariable
                    long Studentid, HttpServletRequest request)
    {
        logger.info(request.getMethod() + " " + request.getRequestURI() + " just accessed! Parameter: " + Studentid);

        studentService.update(updateStudent, Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "delete a student using studentid")
    @ApiResponses(value = {@ApiResponse(code = 200,
            message = "Student Deleted"), @ApiResponse(code = 404,
            message = "Student Not Found",
            response = ErrorDetail.class)})
    @DeleteMapping("/student/{Studentid}")
    public ResponseEntity<?> deleteStudentById(
            @ApiParam(value = "Student Id",
                    required = true,
                    example = "1")
            @PathVariable
                    long Studentid, HttpServletRequest request)
    {
        logger.info(request.getMethod() + " " + request.getRequestURI() + " just accessed! Parameter: " + Studentid);

        studentService.delete(Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // add student to course
    // PUT -- localhost:2019/students/student/{studentid}/course/{courseid}
    //    @PutMapping(value = "/student/{studentid}/course/{courseid}")
    //    public ResponseEntity<?> addStudentToCourse(@PathVariable long studentid, @PathVariable long courseid)
    //    {
    //
    //        return new ResponseEntity<>(HttpStatus.OK);
    //    }

}