import { useState, useEffect, useCallback } from 'react';
import { getProcessingJobs } from '../services/api';
import { formatErrorMessage } from '../utils/errorHandling';
import { UI_CONFIG } from '../config';

/**
 * Custom hook for fetching and polling job status
 * @param {number} pollInterval - Interval in ms between polling
 * @returns {Object} State and control functions for jobs
 */

const useJobPolling = (pollInterval = UI_CONFIG.JOB_POLLING_INTERVAL) => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [polling, setPolling] = useState(true);

  const fetchJobs = useCallback(async () => {
    try {
      setLoading(true);
      const response = await getProcessingJobs();
      setJobs(response.data?.data || []);
      setError(null);
    } catch (err) {
      console.error('Error fetching jobs:', err);
      setError(formatErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  // Manual refresh function
  const refresh = useCallback(() => {
    return fetchJobs();
  }, [fetchJobs]);

  // Toggle polling on/off
  const togglePolling = useCallback(() => {
    setPolling(prevState => !prevState);
  }, []);

  // Set up polling
  useEffect(() => {
    // Initial fetch
    fetchJobs();

    // Only set up interval if polling is enabled
    if (!polling) return;

    const interval = setInterval(() => {
      fetchJobs();
    }, pollInterval);

    // Clean up interval on unmount or when polling changes
    return () => clearInterval(interval);
  }, [fetchJobs, pollInterval, polling]);

  return {
    jobs,
    loading,
    error,
    refresh,
    polling,
    togglePolling
  };
};

export default useJobPolling;
