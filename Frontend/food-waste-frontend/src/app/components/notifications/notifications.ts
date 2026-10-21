import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Notification as NotificationService } from '../../services/notification';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-notifications',
  standalone: true,
<<<<<<< HEAD
  imports: [DatePipe],
=======
  imports: [CommonModule, DatePipe],
>>>>>>> 64475a0 (complete)
  templateUrl: './notifications.html',
  styleUrl: './notifications.css'
})
export class NotificationsComponent implements OnInit {

  notifications: any[] = [];
  private refreshInterval: any;

  constructor(
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    
    // Auto-refresh every 10 seconds for real-time updates
    this.refreshInterval = setInterval(() => {
      this.loadNotifications();
    }, 10000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  loadNotifications(): void {
    this.notificationService.getMyNotifications().subscribe({
      next: (data: any) => {
        this.notifications = data;
      },
      error: (error: any) => {
        console.error('Error loading notifications:', error);
      }
    });
  }

  get unreadCount(): number {
    return this.notifications.filter(
      notification => notification.isRead === false
    ).length;
  }

  markRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        const notification = this.notifications.find(
          item => item.id === id
        );

        if (notification) {
          notification.isRead = true;
        }
      },
      error: (error: any) => {
        console.error('Error marking notification as read:', error);
      }
    });
  }

  markAllRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach(
          notification => notification.isRead = true
        );
      },
      error: (error: any) => {
        console.error('Error marking all notifications as read:', error);
      }
    });
  }
}