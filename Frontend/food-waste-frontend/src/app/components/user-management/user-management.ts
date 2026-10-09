import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Admin } from '../../services/admin';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css'
})
export class UserManagementComponent implements OnInit {

  users: any[] = [];
  searchQuery = '';

  constructor(
    private adminService: Admin
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminService.getAllUsers().subscribe({
      next: (data: any) => {
        this.users = data;
      },
      error: (error: any) => {
        console.error('Error loading users:', error);
      }
    });
  }

  get filteredUsers(): any[] {
    const search = this.searchQuery.toLowerCase().trim();

    if (!search) {
      return this.users;
    }

    return this.users.filter(user =>
      user.name?.toLowerCase().includes(search) ||
      user.email?.toLowerCase().includes(search)
    );
  }

  toggleStatus(userId: number): void {

    const confirmed = window.confirm(
      'Are you sure you want to change this user status?'
    );

    if (!confirmed) {
      return;
    }

    this.adminService.toggleUserStatus(userId).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error: any) => {
        console.error('Error changing user status:', error);
      }
    });
  }
}