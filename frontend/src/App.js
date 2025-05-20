import React, { useState } from 'react';
import styled from 'styled-components';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import FileUploader from './components/FileUploader';
import JobsList from './components/JobsList';

const AppContainer = styled.div`
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
`;

const Header = styled.header`
  text-align: center;
  margin-bottom: 30px;
`;

const Title = styled.h1`
  color: #2196f3;
  margin-bottom: 10px;
`;

const Subtitle = styled.p`
  color: #757575;
  font-size: 18px;
`;

const Section = styled.section`
  margin-bottom: 30px;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
`;

const SectionTitle = styled.h2`
  color: #333;
  margin-top: 0;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
`;

function App() {
  const [refreshJobs, setRefreshJobs] = useState(0);
  
  const handleUploadSuccess = (data) => {
    // Trigger jobs list refresh
    setRefreshJobs(prev => prev + 1);
  };

  return (
    <>
      <AppContainer>
        <Header>
          <Title>IHD Analytics Pipeline</Title>
          <Subtitle>Upload and process JSON feedback files</Subtitle>
        </Header>
        
        <Section>
          <SectionTitle>Upload JSON Files</SectionTitle>
          <FileUploader 
            onUploadSuccess={handleUploadSuccess} 
          />
        </Section>
        
        <Section>
          <JobsList key={refreshJobs} />
        </Section>
      </AppContainer>
      
      <ToastContainer 
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </>
  );
}

export default App;
