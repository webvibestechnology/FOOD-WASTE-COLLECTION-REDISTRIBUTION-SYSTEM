import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Auth } from '../services/auth';

export const donorGuard: CanActivateFn = () => {

  const authService = inject(Auth);
  const router = inject(Router);

  if (authService.isDonor()) {
    return true;
  }

  router.navigate(['/']);
  return false;
};