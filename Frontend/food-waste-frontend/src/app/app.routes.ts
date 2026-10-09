
import { Routes } from '@angular/router';

import { HomeComponent } from './components/home/home';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password';
import { AboutComponent } from './components/about/about';
import { ContactComponent } from './components/contact/contact';

import { NotificationsComponent } from './components/notifications/notifications';

import { DonorDashboardComponent } from './components/donor-dashboard/donor-dashboard';
import { DonorProfileComponent } from './components/donor-profile/donor-profile';
import { FoodDonationComponent } from './components/food-donation/food-donation';
import { DonationHistoryComponent } from './components/donation-history/donation-history';

import { NgoDashboardComponent } from './components/ngo-dashboard/ngo-dashboard';
import { FoodRequestComponent } from './components/food-request/food-request';
import { AvailableFoodComponent } from './components/available-food/available-food';

import { VolunteerDashboardComponent } from './components/volunteer-dashboard/volunteer-dashboard';
import { PickupRequestsComponent } from './components/pickup-requests/pickup-requests';
import { PickupDetailsComponent } from './components/pickup-details/pickup-details';

import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard';
import { UserManagementComponent } from './components/user-management/user-management';
import { DonationManagementComponent } from './components/donation-management/donation-management';
import { NgoManagementComponent } from './components/ngo-management/ngo-management';
import { VolunteerManagementComponent } from './components/volunteer-management/volunteer-management';
import { ReportsComponent } from './components/reports/reports';

import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';
import { donorGuard } from './guards/donor-guard';
import { ngoGuard } from './guards/ngo-guard';
import { volunteerGuard } from './guards/volunteer-guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },

  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: 'register',
    component: RegisterComponent
  },

  {
    path: 'forgot-password',
    component: ForgotPasswordComponent
  },

  {
    path: 'about',
    component: AboutComponent
  },

  {
    path: 'contact',
    component: ContactComponent
  },

  {
    path: 'notifications',
    component: NotificationsComponent,
    canActivate: [authGuard]
  },

  // Donor
  {
    path: 'donor/dashboard',
    component: DonorDashboardComponent,
    canActivate: [authGuard, donorGuard]
  },

  {
    path: 'donor/profile',
    component: DonorProfileComponent,
    canActivate: [authGuard, donorGuard]
  },

  {
    path: 'donor/food-donation',
    component: FoodDonationComponent,
    canActivate: [authGuard, donorGuard]
  },

  {
    path: 'donor/donation-history',
    component: DonationHistoryComponent,
    canActivate: [authGuard, donorGuard]
  },

  // NGO
  {
    path: 'ngo/dashboard',
    component: NgoDashboardComponent,
    canActivate: [authGuard, ngoGuard]
  },

  {
    path: 'ngo/food-request',
    component: FoodRequestComponent,
    canActivate: [authGuard, ngoGuard]
  },

  {
    path: 'ngo/available-food',
    component: AvailableFoodComponent,
    canActivate: [authGuard, ngoGuard]
  },

  // Volunteer
  {
    path: 'volunteer/dashboard',
    component: VolunteerDashboardComponent,
    canActivate: [authGuard, volunteerGuard]
  },

  {
    path: 'volunteer/pickup-requests',
    component: PickupRequestsComponent,
    canActivate: [authGuard, volunteerGuard]
  },

  {
    path: 'volunteer/pickup-details/:id',
    component: PickupDetailsComponent,
    canActivate: [authGuard, volunteerGuard]
  },

  // Admin
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/users',
    component: UserManagementComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/donations',
    component: DonationManagementComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/ngos',
    component: NgoManagementComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/volunteers',
    component: VolunteerManagementComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/reports',
    component: ReportsComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: '**',
    redirectTo: ''
  }
];

