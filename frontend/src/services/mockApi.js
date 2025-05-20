/**
 * Mock API functions for testing the frontend
 * Use this when the backend is not available
 */

// Sample mock data
export const mockJobs = [
  {
    id: 'job-001',
    filename: 'Feedback_50k_1.json',
    status: 'COMPLETED',
    startTime: new Date(Date.now() - 3600000).toISOString(), // 1 hour ago
    endTime: new Date(Date.now() - 3540000).toISOString(), // 59 minutes ago
    message: 'Processed 50,000 records successfully'
  },
  {
    id: 'job-002',
    filename: 'Feedback_50k_2.json',
    status: 'PROCESSING',
    startTime: new Date(Date.now() - 600000).toISOString(), // 10 minutes ago
  },
  {
    id: 'job-003',
    filename: 'Feedback_50k_3.json',
    status: 'FAILED',
    startTime: new Date(Date.now() - 1800000).toISOString(), // 30 minutes ago
    endTime: new Date(Date.now() - 1790000).toISOString(), // 29 minutes and 50 seconds ago
    errorDetails: 'Invalid JSON format in line 42135'
  }
];

// Mock file upload function
export const mockUploadFile = (file, onUploadProgress) => {
  return new Promise((resolve, reject) => {
    // Simulate validation
    if (!file.name.endsWith('.json')) {
      reject('Only JSON files are allowed');
      return;
    }
    
    // Random chance of failure for testing error handling
    const shouldFail = Math.random() < 0.1; // 10% chance of failure
    
    if (shouldFail) {
      // Simulate some progress before failing
      let failProgress = 0;
      const failInterval = setInterval(() => {
        failProgress += 10;
        if (onUploadProgress) {
          onUploadProgress({ loaded: failProgress, total: 100 });
        }
        
        if (failProgress >= 60) {
          clearInterval(failInterval);
          reject({
            response: {
              status: 500,
              data: {
                message: 'Simulated server error during file upload'
              }
            }
          });
        }
      }, 300);
      return;
    }
    
    // Simulate upload progress - speed based on file size
    const fileSize = file.size;
    const stepSize = fileSize > 1000000 ? 2 : 5; // Smaller steps for larger files
    let progress = 0;
    
    const interval = setInterval(() => {
      progress += stepSize;
      if (onUploadProgress) {
        onUploadProgress({ loaded: progress, total: 100 });
      }
      
      if (progress >= 100) {
        clearInterval(interval);
        
        // Add a new job to the mock data
        const jobId = `job-${Date.now()}`;
        const newJob = {
          id: jobId,
          filename: file.name,
          status: 'PROCESSING',
          startTime: new Date().toISOString(),
        };
        
        mockJobs.push(newJob);
        
        // Start a mock processing job
        setTimeout(() => {
          // 80% chance of success
          if (Math.random() < 0.8) {
            const jobIndex = mockJobs.findIndex(j => j.id === jobId);
            if (jobIndex !== -1) {
              mockJobs[jobIndex] = {
                ...mockJobs[jobIndex],
                status: 'COMPLETED',
                endTime: new Date().toISOString(),
                message: `Processed ${Math.floor(fileSize / 1000)} records successfully`
              };
            }
          } else {
            // Job failed
            const jobIndex = mockJobs.findIndex(j => j.id === jobId);
            if (jobIndex !== -1) {
              mockJobs[jobIndex] = {
                ...mockJobs[jobIndex],
                status: 'FAILED',
                endTime: new Date().toISOString(),
                errorDetails: 'Simulated processing error: Invalid data format in file'
              };
            }
          }
        }, 5000 + Math.random() * 10000); // Complete between 5-15 seconds
        
        // Simulate successful response
        resolve({
          data: {
            jobId: jobId,
            message: 'File uploaded successfully'
          }
        });
      }
    }, 200);
  });
};

// Mock get all jobs function
export const mockGetProcessingJobs = () => {
  return Promise.resolve({
    data: {
      success: true,
      message: "Jobs retrieved successfully",
      data: mockJobs
    }
  });
};

// Mock get job status function
export const mockGetJobStatus = (jobId) => {
  const job = mockJobs.find(j => j.id === jobId);
  if (!job) {
    return Promise.reject({
      response: {
        status: 404,
        data: {
          success: false,
          message: `Job ${jobId} not found`,
          data: null
        }
      }
    });
  }

  return Promise.resolve({
    data: {
      success: true,
      message: "Job status retrieved successfully",
      data: job
    }
  });
};

export default {
  mockUploadFile,
  mockGetProcessingJobs,
  mockGetJobStatus,
  mockJobs
};
