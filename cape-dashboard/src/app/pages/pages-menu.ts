import { NbMenuItem } from '@nebular/theme';

export const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'general.menu.dashboard',
    icon: 'pie-chart-outline',
    link: '/pages/dashboard',
    home: true,
  },
  {
    title: 'general.menu.consents_logs_group',
    group: true,
  },
  {
    title: 'general.menu.data_event_logs',
    icon: 'archive-outline',
    link: '/pages/auditlogs',
  },
  {
    title: 'general.menu.consents',
    icon: 'toggle-left-outline',
    link: '/pages/consents',
  },
  {
    title: 'general.menu.data_control_flow',
    icon: 'swap-outline',
    link: '/pages/consents/controlflow',
  },
  {
    title: 'general.menu.services',
    group: true,
  },
  {
    title: 'general.menu.available_services',
    icon: 'grid-outline',
    link: '/pages/services/availableServices',
  },

  {
    title: 'general.menu.linked_services',
    icon: 'link-2-outline',
    link: '/pages/services/linkedServices',
  },

  {
    title: 'general.menu.data_connectors',
    icon: 'cube-outline',
    link: '/pages/tables/linked_services',
  },
];
