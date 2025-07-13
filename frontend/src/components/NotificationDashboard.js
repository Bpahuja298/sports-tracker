import React, { useState, useEffect } from 'react';
import axios from 'axios';

const NotificationDashboard = () => {
    const [notifications, setNotifications] = useState([]);
    const [stats, setStats] = useState({});
    const [health, setHealth] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchData();
        // Refresh data every 30 seconds
        const interval = setInterval(fetchData, 30000);
        return () => clearInterval(interval);
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [notificationsRes, statsRes, healthRes] = await Promise.all([
                axios.get('/api/notifications/recent?limit=20'),
                axios.get('/api/notifications/stats'),
                axios.get('/api/notifications/health')
            ]);

            setNotifications(notificationsRes.data);
            setStats(statsRes.data);
            setHealth(healthRes.data);
            setError(null);
        } catch (err) {
            setError('Failed to fetch notification data');
            console.error('Error fetching notification data:', err);
        } finally {
            setLoading(false);
        }
    };

    const formatDateTime = (dateTimeString) => {
        return new Date(dateTimeString).toLocaleString();
    };

    const getStatusBadge = (status) => {
        const statusClass = status === 'healthy' ? 'bg-success' : 'bg-danger';
        return <span className={`badge ${statusClass}`}>{status}</span>;
    };

    if (loading && notifications.length === 0) {
        return (
            <div className="container mt-4">
                <div className="text-center">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <p className="mt-2">Loading notification system...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-4">
            <div className="row">
                <div className="col-12">
                    <h1 className="mb-4">
                        🎾 Tennis Match Notification System
                    </h1>
                    
                    {error && (
                        <div className="alert alert-danger" role="alert">
                            {error}
                        </div>
                    )}

                    {/* System Status */}
                    <div className="row mb-4">
                        <div className="col-md-6">
                            <div className="card">
                                <div className="card-header">
                                    <h5 className="card-title mb-0">System Status</h5>
                                </div>
                                <div className="card-body">
                                    <div className="row">
                                        <div className="col-6">
                                            <strong>Status:</strong> {getStatusBadge(health.status)}
                                        </div>
                                        <div className="col-6">
                                            <strong>Scheduler:</strong> 
                                            <span className={`badge ${health.schedulerActive ? 'bg-success' : 'bg-danger'} ms-2`}>
                                                {health.schedulerActive ? 'Active' : 'Inactive'}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="row mt-2">
                                        <div className="col-12">
                                            <small className="text-muted">
                                                Last Check: {health.lastCheck ? formatDateTime(health.lastCheck) : 'N/A'}
                                            </small>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="col-md-6">
                            <div className="card">
                                <div className="card-header">
                                    <h5 className="card-title mb-0">Statistics</h5>
                                </div>
                                <div className="card-body">
                                    <div className="row">
                                        <div className="col-6">
                                            <div className="text-center">
                                                <h4 className="text-primary">{stats.totalNotificationsSent || 0}</h4>
                                                <small>Total Notifications</small>
                                            </div>
                                        </div>
                                        <div className="col-6">
                                            <div className="text-center">
                                                <h4 className="text-success">{stats.todayNotifications || 0}</h4>
                                                <small>Today's Notifications</small>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="row mt-2">
                                        <div className="col-6">
                                            <div className="text-center">
                                                <h4 className="text-info">{stats.totalScheduledMatches || 0}</h4>
                                                <small>Scheduled Matches</small>
                                            </div>
                                        </div>
                                        <div className="col-6">
                                            <div className="text-center">
                                                <h4 className="text-warning">{stats.matchesWithNotifications || 0}</h4>
                                                <small>With Notifications</small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* How It Works */}
                    <div className="card mb-4">
                        <div className="card-header">
                            <h5 className="card-title mb-0">How It Works</h5>
                        </div>
                        <div className="card-body">
                            <div className="row">
                                <div className="col-md-4">
                                    <div className="text-center">
                                        <div className="mb-2">
                                            <i className="fas fa-clock fa-2x text-primary"></i>
                                        </div>
                                        <h6>Monitor Matches</h6>
                                        <p className="small text-muted">
                                            System checks for upcoming tennis matches every minute using RapidAPI
                                        </p>
                                    </div>
                                </div>
                                <div className="col-md-4">
                                    <div className="text-center">
                                        <div className="mb-2">
                                            <i className="fas fa-bell fa-2x text-warning"></i>
                                        </div>
                                        <h6>Send Alerts</h6>
                                        <p className="small text-muted">
                                            Notifications are sent exactly 10 minutes before each match starts
                                        </p>
                                    </div>
                                </div>
                                <div className="col-md-4">
                                    <div className="text-center">
                                        <div className="mb-2">
                                            <i className="fas fa-tennis-ball fa-2x text-success"></i>
                                        </div>
                                        <h6>Never Miss a Match</h6>
                                        <p className="small text-muted">
                                            Get timely alerts for all tennis matches with real start times
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Recent Notifications */}
                    <div className="card">
                        <div className="card-header d-flex justify-content-between align-items-center">
                            <h5 className="card-title mb-0">Recent Notifications</h5>
                            <button 
                                className="btn btn-sm btn-outline-primary" 
                                onClick={fetchData}
                                disabled={loading}
                            >
                                {loading ? (
                                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                                ) : (
                                    <i className="fas fa-sync-alt me-2"></i>
                                )}
                                Refresh
                            </button>
                        </div>
                        <div className="card-body">
                            {notifications.length === 0 ? (
                                <div className="text-center py-4">
                                    <i className="fas fa-bell-slash fa-3x text-muted mb-3"></i>
                                    <h6 className="text-muted">No notifications yet</h6>
                                    <p className="text-muted">
                                        Notifications will appear here when matches are about to start
                                    </p>
                                </div>
                            ) : (
                                <div className="list-group">
                                    {notifications.map((notification, index) => (
                                        <div key={index} className="list-group-item">
                                            <div className="d-flex w-100 justify-content-between">
                                                <h6 className="mb-1">
                                                    🎾 {notification.homePlayer} vs {notification.awayPlayer}
                                                </h6>
                                                <small className="text-muted">
                                                    {formatDateTime(notification.sentAt)}
                                                </small>
                                            </div>
                                            <p className="mb-1">
                                                <strong>Match Time:</strong> {formatDateTime(notification.matchDateTime)}
                                            </p>
                                            <p className="mb-1">
                                                <strong>Venue:</strong> {notification.venue || 'TBD'}
                                            </p>
                                            <small className="text-success">
                                                <i className="fas fa-check-circle me-1"></i>
                                                Notification sent successfully
                                            </small>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default NotificationDashboard;