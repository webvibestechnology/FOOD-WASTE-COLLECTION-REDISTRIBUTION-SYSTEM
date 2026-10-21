
import { Routes } from '@angular/router';

import { HomeComponent } from './components/home/home';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password';
import { AboutComponent } from './components/about/about';
import { ContactComponent } from './components/contact/contact';

import { NotificationsComponent } from './components/notifications/notifications';

<<<<<<< HEAD
=======
// Donor Components
>>>>>>> 64475a0 (complete)
import { DonorDashboardComponent } from './components/donor-dashboard/donor-dashboard';
import { DonorProfileComponent } from './components/donor-profile/donor-profile';
import { FoodDonationComponent } from './components/food-donation/food-donation';
import { DonationHistoryComponent } from './components/donation-history/donation-history';

<<<<<<< HEAD
import { NgoDashboardComponent } from './components/ngo-dashboard/ngo-dashboard';
import { FoodRequestComponent } from './components/food-request/food-request';
import { AvailableFoodComponent } from './components/available-food/available-food';

import { VolunteerDashboardComponent } from './components/volunteer-dashboard/volunteer-dashboard';
import { PickupRequestsComponent } from './components/pickup-requests/pickup-requests';
import { PickupDetailsComponent } from './components/pickup-details/pickup-details';

=======
// NGO Components
import { NgoDashboardComponent } from './components/ngo-dashboard/ngo-dashboard';
import { FoodRequestComponent } from './components/food-request/food-request';
import { AvailableFoodComponent } from './components/available-food/available-food';
import { NgoProfileComponent } from './components/ngo-profile/ngo-profile';

// Volunteer Components
import { VolunteerDashboardComponent } from './components/volunteer-dashboard/volunteer-dashboard';
import { PickupRequestsComponent } from './components/pickup-requests/pickup-requests';
import { PickupDetailsComponent } from './components/pickup-details/pickup-details';
import { VolunteerProfileComponent } from './components/volunteer-profile/volunteer-profile';

// Admin Components
>>>>>>> 64475a0 (complete)
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard';
import { UserManagementComponent } from './components/user-management/user-management';
import { DonationManagementComponent } from './components/donation-management/donation-management';
import { NgoManagementComponent } from './components/ngo-management/ngo-management';
import { VolunteerManagementComponent } from './components/volunteer-management/volunteer-management';
import { ReportsComponent } from './components/reports/reports';
<<<<<<< HEAD

import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';
import { donorGuard } from './guards/donor-guard';
import { ngoGuard } from './guards/ngo-guard';
import { volunteerGuard } from './guards/volunteer-guard';
=======
>>>>>>> 64475a0 (complete)

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
<<<<<<< HEAD
    path: 'ngo/food-request',
=======
    path: 'ngo/profile',
    component: NgoProfileComponent,
    canActivate: [authGuard, ngoGuard]
  },

  {
    path: 'ngo/requests',
>>>>>>> 64475a0 (complete)
    component: FoodRequestComponent,
    canActivate: [authGuard, ngoGuard]
  },

  {
    path: 'ngo/food-requests',
    redirectTo: 'ngo/requests',
    pathMatch: 'full'
  },

  {
    path: 'ngo/available-food',
    component: AvailableFoodComponent,
    canActivate: [authGuard, ngoGuard]
  },

<<<<<<< HEAD
  // Volunteer
=======
  {
    path: 'ngo/pickups',
    redirectTo: 'ngo/requests',
    pathMatch: 'full'
  },


  // =========================
  // VOLUNTEER ROUTES
  // =========================

>>>>>>> 64475a0 (complete)
  {
    path: 'volunteer/dashboard',
    component: VolunteerDashboardComponent,
    canActivate: [authGuard, volunteerGuard]
  },

  {
<<<<<<< HEAD
    path: 'volunteer/pickup-requests',
=======
    path: 'volunteer/profile',
    component: VolunteerProfileComponent,
    canActivate: [authGuard, volunteerGuard]
  },

  {
    path: 'volunteer/pickups',
>>>>>>> 64475a0 (complete)
    component: PickupRequestsComponent,
    canActivate: [authGuard, volunteerGuard]
  },

  {
<<<<<<< HEAD
    path: 'volunteer/pickup-details/:id',
=======
    path: 'volunteer/pickup-requests',
    redirectTo: 'volunteer/pickups',
    pathMatch: 'full'
  },

  {
    path: 'volunteer/pickups/:id',
>>>>>>> 64475a0 (complete)
    component: PickupDetailsComponent,
    canActivate: [authGuard, volunteerGuard]
  },

<<<<<<< HEAD
  // Admin
=======
  {
    path: 'volunteer/pickup-details/:id',
    redirectTo: 'volunteer/pickups/:id'
  },


  // =========================
  // ADMIN ROUTES
  // =========================

>>>>>>> 64475a0 (complete)
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

<<<<<<< HEAD
=======
];
>>>>>>> 64475a0 (complete)
