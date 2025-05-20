import React from 'react';
import styled from 'styled-components';
import useJobPolling from '../hooks/useJobPolling';
import { UI_CONFIG } from '../config';

const JobsContainer = styled.div`
  margin-top: 30px;
`;

const JobsHeader = styled.h2`
  font-size: 24px;
  margin-bottom: 15px;
  color: #333;
`;

const JobsListContainer = styled.div`
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
`;

const JobItem = styled.div`
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
  background-color: ${props => {
    if (props.status === 'COMPLETED') return '#e8f5e9';
    if (props.status === 'FAILED') return '#ffebee';
    if (props.status === 'PROCESSING') return '#e3f2fd';
    return '#fff';
  }};
  
  &:last-child {
    border-bottom: none;
  }
`;

const JobTitle = styled.h3`
  margin: 0 0 5px 0;
  font-size: 18px;
`;

const JobDetail = styled.p`
  margin: 2px 0;
  color: #555;
  font-size: 14px;
`;

const StatusBadge = styled.span`
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  margin-left: 8px;
  color: white;
  background-color: ${props => {
    if (props.status === 'COMPLETED') return '#4caf50';
    if (props.status === 'FAILED') return '#f44336';
    if (props.status === 'PROCESSING') return '#2196f3';
    return '#9e9e9e';
  }};
`;

const RefreshButton = styled.button`
  background-color: #2196f3;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 10px;
  font-weight: bold;
  
  &:hover {
    background-color: #0d8bf2;
  }
`;

const EmptyState = styled.div`
  padding: 30px;
  text-align: center;
  color: #9e9e9e;
`;

const JobsList = () => {
  const { jobs, loading, error, refresh, polling, togglePolling } = useJobPolling();

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  return (
    <JobsContainer>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <JobsHeader>Processing Jobs</JobsHeader>
          {UI_CONFIG.USE_MOCK_API && (
            <div style={{ 
              backgroundColor: '#fff3cd', 
              color: '#856404', 
              padding: '4px 8px', 
              borderRadius: '4px',
              fontSize: '12px',
              marginTop: '5px',
              display: 'inline-block'
            }}>
              Mock Data (API Server Not Connected)
            </div>
          )}
        </div>
        <div>
          <RefreshButton onClick={refresh} disabled={loading} style={{ marginRight: '10px' }}>
            {loading ? 'Refreshing...' : 'Refresh'}
          </RefreshButton>
          <RefreshButton onClick={togglePolling} style={{ backgroundColor: polling ? '#f44336' : '#4caf50' }}>
            {polling ? 'Stop Auto-Refresh' : 'Start Auto-Refresh'}
          </RefreshButton>
        </div>
      </div>
      
      {error && <div style={{ color: 'red', marginBottom: '15px' }}>{error}</div>}
      
      <JobsListContainer>
        {jobs.length === 0 ? (
          <EmptyState>
            {loading ? 'Loading jobs...' : 'No processing jobs found'}
          </EmptyState>
        ) : (
          jobs.map(job => (
            <JobItem key={job.id} status={job.status}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <JobTitle>
                  {job.filename || 'Unknown file'}
                </JobTitle>
                <StatusBadge status={job.status}>{job.status}</StatusBadge>
              </div>
              <JobDetail><strong>Job ID:</strong> {job.id}</JobDetail>
              <JobDetail><strong>Started:</strong> {job.startTime ? formatDate(job.startTime) : 'N/A'}</JobDetail>
              {job.endTime && <JobDetail><strong>Completed:</strong> {formatDate(job.endTime)}</JobDetail>}
              {job.message && <JobDetail><strong>Message:</strong> {job.message}</JobDetail>}
              {job.errorDetails && (
                <JobDetail style={{ color: 'red' }}>
                  <strong>Error:</strong> {job.errorDetails}
                </JobDetail>
              )}
            </JobItem>
          ))
        )}
      </JobsListContainer>
    </JobsContainer>
  );
};

export default JobsList;
