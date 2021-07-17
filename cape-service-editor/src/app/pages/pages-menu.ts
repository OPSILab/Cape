import { NbMenuItem } from '@nebular/theme';

export const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'general.menu.dashboard',
    icon: 'pie-chart-outline',
    link: '/pages',
  },
  {
    title: 'general.menu.consents_logs_group',
    group: true,
  },
  {
    title: 'general.menu.consents',
    icon: 'toggle-left-outline',
    link: '/pages/consents/register',
  },
  {
    title: 'general.menu.services',
    group: true,
  },
  {
    title: 'general.menu.availableservices',
    icon: 'grid-outline',
    link: '/pages/services/availableServices',
    home: true,
  },
  {
    title: 'general.menu.editor',
    icon: 'edit-outline',
    link: '/pages/services/service-editor',
  },
];
