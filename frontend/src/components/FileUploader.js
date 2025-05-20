import React, { useState, useCallback, useEffect } from 'react';
import { useDropzone } from 'react-dropzone';
import styled from 'styled-components';
import { toast } from 'react-toastify';
import { uploadFile } from '../services/api';
import { formatErrorMessage, validateJsonFile } from '../utils/errorHandling';
import { UI_CONFIG, API_CONFIG } from '../config';

const DropzoneContainer = styled.div`
  border: 2px dashed ${props => props.isDragActive ? '#2196f3' : '#eeeeee'};
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  margin-bottom: 20px;
  background-color: ${props => props.isDragActive ? '#e3f2fd' : '#fafafa'};
  transition: all 0.3s ease;
  
  &:hover {
    border-color: #2196f3;
    background-color: #e3f2fd;
  }
`;

const UploadText = styled.p`
  font-size: 18px;
  color: #555;
  margin: 0;
`;

const FileInfo = styled.div`
  margin-top: 15px;
`;

const ProgressContainer = styled.div`
  width: 100%;
  height: 20px;
  background-color: #f5f5f5;
  border-radius: 10px;
  margin-top: 10px;
  margin-bottom: 20px;
  overflow: hidden;
`;

const ProgressBar = styled.div`
  height: 100%;
  background-color: #4caf50;
  width: ${props => props.progress}%;
  transition: width 0.3s ease;
`;

const FileUploader = ({ onUploadSuccess, onUploadError }) => {
  const [uploadProgress, setUploadProgress] = useState(0);
  const [currentFile, setCurrentFile] = useState(null);
  const [isUploading, setIsUploading] = useState(false);

  const resetUploadState = useCallback(() => {
    setUploadProgress(0);
    setCurrentFile(null);
    setIsUploading(false);
  }, []);

  const onDrop = useCallback(acceptedFiles => {
    const file = acceptedFiles[0];
    
    // Validate file
    validateJsonFile(file)
      .then(() => {
        setCurrentFile(file);
        setIsUploading(true);
        
        console.log(`Attempting to upload file: ${file.name} (${file.size} bytes)`);
        console.log(`Using mock API: ${UI_CONFIG.USE_MOCK_API}`);
        
        // Upload file
        return uploadFile(file, (progressEvent) => {
          const percentage = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadProgress(percentage);
          console.log(`Upload progress: ${percentage}%`);
        });
      })
      .then((response) => {
        console.log('Upload successful:', response);
        toast.success('File uploaded successfully!');
        if (onUploadSuccess) {
          onUploadSuccess(response.data);
        }
        setTimeout(resetUploadState, 1500);
      })
      .catch((error) => {
        console.error('Error uploading file:', error);
        
        // Format error message using our utility
        const errorMessage = typeof error === 'string' ? error : formatErrorMessage(error);
        
        // Specific handling for "No static resource" error
        if (error?.message?.includes('No static resource')) {
          toast.error('API server not reachable. Please ensure the API service is running.');
          console.error('The API server is not reachable. This happens when requests are being handled as static files instead of API calls.');
          console.error('Try these troubleshooting steps:');
          console.error('1. Ensure the API service is running at ' + API_CONFIG.BASE_URL);
          console.error('2. Check that the proxy configuration is correct');
          console.error('3. Try enabling mock API mode while troubleshooting');
        } else {
          toast.error(`Error: ${errorMessage}`);
        }
        
        if (onUploadError) {
          onUploadError(error);
        }
        setIsUploading(false);
      });
  }, [onUploadSuccess, onUploadError, resetUploadState]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ 
    onDrop, 
    accept: {
      'application/json': ['.json'],
    },
    disabled: isUploading,
    multiple: false,
  });

  return (
    <div>
      {UI_CONFIG.USE_MOCK_API && (
        <div style={{ 
          backgroundColor: '#fff3cd', 
          color: '#856404', 
          padding: '8px 12px', 
          borderRadius: '4px',
          fontSize: '13px',
          marginBottom: '15px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}>
          <span>
            <strong>Mock Mode:</strong> Files will be processed with mock data (API Server Not Connected)
          </span>
        </div>
      )}
      
      <DropzoneContainer {...getRootProps()} isDragActive={isDragActive}>
        <input {...getInputProps()} />
        {isDragActive ? (
          <UploadText>Drop the JSON file here...</UploadText>
        ) : (
          <UploadText>Drag & drop a JSON file here, or click to select a file</UploadText>
        )}
        {currentFile && (
          <FileInfo>
            <p>File: {currentFile.name} ({Math.round(currentFile.size / 1024)} KB)</p>
          </FileInfo>
        )}
      </DropzoneContainer>
      
      {isUploading && (
        <ProgressContainer>
          <ProgressBar progress={uploadProgress} />
        </ProgressContainer>
      )}
    </div>
  );
};

export default FileUploader;
