package com.ewis.jobms.job;

import com.ewis.jobms.job.dto.JobWithCompanyDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private JobService jobservice;

    public JobController(JobService jobservice) {
        this.jobservice = jobservice;
    }

    @GetMapping
    public ResponseEntity<List<JobWithCompanyDTO>> findAll () {
        return new ResponseEntity<>(jobservice.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody Job job){
        jobservice.createJob(job);
        return new ResponseEntity<>("Job added successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobWithCompanyDTO> getJobById(@PathVariable Long id){
        JobWithCompanyDTO jobWithCompanyDTO = jobservice.getJobById(id);
        if(jobWithCompanyDTO != null) {
            return new ResponseEntity<>(jobWithCompanyDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobById(@PathVariable Long id) {
        boolean deletedJob = jobservice.deleteJobById(id);
        if (deletedJob == true) {
            return new ResponseEntity<>("Job deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateJobById(@PathVariable Long id, @RequestBody Job updatedJob) {
        boolean updated = jobservice.updateJobById(id, updatedJob);
        if (updated) {
            return new ResponseEntity<>("Job updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);
        }
    }
}
