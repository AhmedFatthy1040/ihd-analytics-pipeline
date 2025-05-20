# IHD Analytics Pipeline Frontend

This is the frontend application for the IHD Analytics Pipeline, built with React.

## Features

* JSON file upload with drag & drop interface
* Upload progress indicator
* Success/error notifications
* File validation (.json only)
* Processing jobs status dashboard with auto-refresh
* Mock API support for development without backend

## Getting Started

### Prerequisites

* Node.js (v22.15.1)
* npm

### Installation

1. Install dependencies:
```bash
npm install
```

2. Set up environment variables (optional):
   Copy the `.env` file to `.env.local` and modify as needed:
```bash
cp .env .env.local
```

3. Start the development server:
```bash
# Using npm
npm start

# Or using the provided script
./start.sh
```

The application will be available at `http://localhost:3000`.

## Development Modes

### Mock API Mode

By default, the application runs in mock API mode, which simulates the backend API responses. This allows you to develop and test the frontend without needing to run the backend API service.

To enable/disable mock API mode:

1. Set the environment variable in `.env.local`:
```
REACT_APP_USE_MOCK_API=true
```

2. When mock API is enabled, you'll see indicators in the UI showing you're using mock data.

### Production Mode

For production use, you should:

1. Disable mock API:
```
REACT_APP_USE_MOCK_API=false
```

2. Ensure the API service is running and accessible at the configured URL.

## Environment Variables

You can customize the application behavior using environment variables:

- `REACT_APP_API_URL` - Backend API URL (default: http://localhost:8080)
- `REACT_APP_USE_MOCK_API` - Use mock API instead of real backend (default: true)
- `REACT_APP_ENABLE_AUTO_POLLING` - Enable auto-refresh of jobs list (default: true)
- `REACT_APP_POLLING_INTERVAL` - Interval in ms for polling job status (default: 10000)

## Building for Production

To create a production build:

```bash
npm run build
```

This creates a production-ready build in the `build` directory that can be deployed to any static hosting service.

## Docker Deployment

The frontend includes a Docker configuration for easy deployment:

```bash
# Build the Docker image
docker build -t ihd-analytics-frontend .

# Run the Docker container
docker run -p 3000:80 ihd-analytics-frontend
```

When running with Docker Compose from the project root, the frontend will be automatically built and deployed.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

### Code Splitting

This section has moved here: [https://facebook.github.io/create-react-app/docs/code-splitting](https://facebook.github.io/create-react-app/docs/code-splitting)

### Analyzing the Bundle Size

This section has moved here: [https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size](https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size)

### Making a Progressive Web App

This section has moved here: [https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app](https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app)

### Advanced Configuration

This section has moved here: [https://facebook.github.io/create-react-app/docs/advanced-configuration](https://facebook.github.io/create-react-app/docs/advanced-configuration)

### Deployment

This section has moved here: [https://facebook.github.io/create-react-app/docs/deployment](https://facebook.github.io/create-react-app/docs/deployment)

### `npm run build` fails to minify

This section has moved here: [https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify](https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify)
