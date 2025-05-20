/**
 * Application configuration
 */

// API Configuration
export const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  ENDPOINTS: {
    UPLOAD: '/api/v1/feedback/upload',
    JOBS: '/api/v1/jobs',
    JOB_STATUS: (id) => `/api/v1/jobs/${id}`
  }
};

// Upload Configuration
export const UPLOAD_CONFIG = {
  MAX_FILE_SIZE: 100 * 1024 * 1024, // 100MB
  ALLOWED_EXTENSIONS: ['.json'],
  MIME_TYPES: ['application/json']
};

// UI Configuration
export const UI_CONFIG = {
  JOB_POLLING_INTERVAL: parseInt(process.env.REACT_APP_POLLING_INTERVAL) || 10000, // 10 seconds
  TOAST_AUTO_CLOSE: 5000, // 5 seconds
  USE_MOCK_API: process.env.REACT_APP_USE_MOCK_API === 'true'
};

// Log configuration on startup (for debugging)
console.log('Frontend configuration:', {
  API_BASE_URL: API_CONFIG.BASE_URL,
  USE_MOCK_API: UI_CONFIG.USE_MOCK_API,
  POLLING_INTERVAL: UI_CONFIG.JOB_POLLING_INTERVAL
});

export default {
  API_CONFIG,
  UPLOAD_CONFIG,
  UI_CONFIG
};
