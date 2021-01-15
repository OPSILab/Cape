import { NbMenuItem } from '@nebular/theme';

export const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'SERVICES',
    group: true,
  },
  {
    title: 'My Wellness Service',
    icon: 'grid-outline',
    link: '/pages/services/my-wellness'
  },
  {
    title: 'My Measurements Service',
    icon: 'grid-outline',
    link: '/pages/services/my-measurements'
  }
];
