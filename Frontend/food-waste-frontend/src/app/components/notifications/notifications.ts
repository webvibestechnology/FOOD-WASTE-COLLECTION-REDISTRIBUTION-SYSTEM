import { Component, OnInit } from '@angular/core';
import { Notification as NotificationService } from '../../services/notification';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css'
})
export class NotificationsComponent implements OnInit {

  notifications: any[] = [];

  constructor(
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
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