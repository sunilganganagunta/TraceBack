import React, { useEffect, useState } from 'react';
import { notificationsApi } from '../api/client';
import { Notification } from '../types';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import { Bell, CheckCheck, Clock, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export const NotificationsPage: React.FC = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const loadNotifications = async () => {
    setIsLoading(true);
    try {
      const data = await notificationsApi.getAll(0, 50);
      setNotifications(data.content);
    } catch (err) {
      console.error('Failed to load notifications', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, []);

  const handleMarkAsRead = async (id: string) => {
    try {
      await notificationsApi.markAsRead(id);
      setNotifications(prev =>
        prev.map(n => n.id === id ? { ...n, read: true } : n)
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationsApi.markAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, read: true })));
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="space-y-6 max-w-3xl mx-auto pb-12">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-navy-900">Notifications</h1>
          <p className="text-xs text-slate-500 mt-1">
            Real-time updates regarding match alerts, recovery claims, and review decisions
          </p>
        </div>

        {notifications.some(n => !n.read) && (
          <Button size="sm" variant="outline" onClick={handleMarkAllRead}>
            <CheckCheck className="w-4 h-4 mr-1" /> Mark All as Read
          </Button>
        )}
      </div>

      {isLoading ? (
        <div className="text-center py-16">
          <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
          <p className="mt-2 text-xs text-slate-500">Loading your notifications...</p>
        </div>
      ) : notifications.length === 0 ? (
        <EmptyState
          icon={Bell}
          title="No Notifications Yet"
          description="You're all caught up! New alerts regarding item matches and submitted claims will appear here."
        />
      ) : (
        <div className="space-y-3">
          {notifications.map((n) => (
            <Card
              key={n.id}
              className={`p-4 transition-colors ${
                !n.read ? 'border-l-4 border-l-primary-500 bg-primary-50/20' : 'bg-white'
              }`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-bold text-slate-900">{n.title}</span>
                    {!n.read && (
                      <span className="w-2 h-2 rounded-full bg-primary-600" />
                    )}
                  </div>
                  <p className="text-xs text-slate-600">{n.message}</p>
                  <div className="flex items-center gap-1 text-[11px] text-slate-400 pt-1">
                    <Clock className="w-3 h-3" />
                    <span>{new Date(n.createdAt).toLocaleString()}</span>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {!n.read && (
                    <button
                      onClick={() => handleMarkAsRead(n.id)}
                      className="text-xs text-slate-400 hover:text-slate-600 p-1"
                      title="Mark as read"
                    >
                      <CheckCheck className="w-4 h-4" />
                    </button>
                  )}
                  {n.relatedResourceId && (
                    <Link
                      to={n.relatedResourceType === 'REPORT' || n.relatedResourceType === 'MATCH'
                        ? `/reports/${n.relatedResourceId}`
                        : `/my-claims`}
                    >
                      <Button size="sm" variant="ghost">
                        <ArrowRight className="w-3.5 h-3.5" />
                      </Button>
                    </Link>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};
