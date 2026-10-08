import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  ngOnInit(): void {

    if (this.authService.isLoggedIn()) {

      const role = this.authService.getUserRole();

      if (role === 'DONOR') {
        this.router.navigate(['/donor/dashboard']);

      } else if (role === 'NGO') {
        this.router.navigate(['/ngo/dashboard']);

      } else if (role === 'VOLUNTEER') {
        this.router.navigate(['/volunteer/dashboard']);

      } else if (role === 'ADMIN') {
        this.router.navigate(['/admin/dashboard']);
      }
    }
  }
}