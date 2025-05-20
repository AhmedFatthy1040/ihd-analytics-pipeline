// Error handling utility functions
import { UI_CONFIG } from '../config';

/**
 * Format error message from API response
 * @param {Error} error - The error object from axios
 * @returns {string} Formatted error message
 */
export const formatErrorMessage = (error) => {
  // Handle string errors
  if (typeof error === 'string') {
    return error;
  }
  
  // Special handling for "No static resource" error when API server is not available
  if (error?.message?.includes('No static resource') && UI_CONFIG.USE_MOCK_API) {
    return 'API server is not connected. Using mock data instead.';
  }
  
  if (error.response) {
    // Server responded with a status code outside of 2xx range
    const data = error.response.data;
    
    if (data.message) {
      return data.message;
    } else if (typeof data === 'string') {
      return data;
    } else {
      return `Error: ${error.response.status} ${error.response.statusText}`;
    }
  } else if (error.request) {
    // Request was made but no response received
    if (UI_CONFIG.USE_MOCK_API) {
      return 'API server is not available. Using mock data.';
    }
    return 'Server did not respond. Please check your connection.';
  } else {
    // Something else caused the error
    return error.message || 'An unknown error occurred';
  }
};

/**
 * Check if a file is a valid JSON file
 * @param {File} file - The file to check
 * @returns {Promise<boolean>} True if valid JSON
 */
export const validateJsonFile = (file) => {
  return new Promise((resolve, reject) => {
    // Check file extension
    if (!file.name.toLowerCase().endsWith('.json')) {
      reject('File must have a .json extension');
      return;
    }
    
    // For larger files, we might only want to check the extension
    if (file.size > 10 * 1024 * 1024) { // 10MB
      resolve(true);
      return;
    }
    
    // For smaller files, we can also validate if it's valid JSON
    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const content = event.target.result;
        JSON.parse(content);
        resolve(true);
      } catch (e) {
        reject('The file does not contain valid JSON');
      }
    };
    reader.onerror = () => reject('Error reading the file');
    reader.readAsText(file);
  });
};
