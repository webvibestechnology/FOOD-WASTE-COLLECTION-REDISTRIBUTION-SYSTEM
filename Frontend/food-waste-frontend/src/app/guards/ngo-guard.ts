import { CanActivateFn } from '@angular/router';

export const ngoGuard: CanActivateFn = (route, state) => {
  return true;
};
