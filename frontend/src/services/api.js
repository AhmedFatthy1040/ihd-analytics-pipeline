import axios from 'axios';
import { API_CONFIG, UI_CONFIG } from '../config';
import { 
  mockUploadFile, 
  mockGetProcessingJobs, 
  mockGetJobStatus,
  mockJobs
} from './mockApi';

// Use mock API flag from config
const USE_MOCK_API = UI_CONFIG.USE_MOCK_API;

const api = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// File upload API
export const uploadFile = (file, onUploadProgress) => {
  if (USE_MOCK_API) {
    return mockUploadFile(file, onUploadProgress);
  }
  
  const formData = new FormData();
  formData.append('file', file);
  
  return api.post(API_CONFIG.ENDPOINTS.UPLOAD, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    onUploadProgress,
  });
};

// Get all processing jobs
export const getProcessingJobs = () => {
  if (USE_MOCK_API) {
    return Promise.resolve({ 
      data: {
        success: true,
        message: "Jobs retrieved successfully",
        data: mockJobs
      }
    });
  }
  return api.get(API_CONFIG.ENDPOINTS.JOBS);
};

// Get job status by id
export const getJobStatus = (jobId) => {
  if (USE_MOCK_API) {
    return mockGetJobStatus(jobId);
  }
  return api.get(API_CONFIG.ENDPOINTS.JOB_STATUS(jobId));
};

export default api;
