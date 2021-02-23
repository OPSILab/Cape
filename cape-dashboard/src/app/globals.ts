import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class Globals {
  readonly BASE_URL = '/api/v1';
  readonly WIDGETS_URL = `${this.BASE_URL}/widgets`;
  readonly PERSONAL_DATA_URL = `${this.BASE_URL}/personal-data`;
  readonly PURPOSE_URL = `${this.BASE_URL}/purpose`;
  readonly CONTEXT_URL = `${this.BASE_URL}/context`;
  readonly DATA_STORE_URL = `${this.BASE_URL}/stored-data`;
  readonly PERSONAL_PROCESSED_URL = `${this.BASE_URL}/personal-vs-processed-data`;
  readonly GRAPH_DATA_URL = `${this.BASE_URL}/graph-data`;
  readonly TIMELINE_URL = `${this.BASE_URL}/timeline-data`;
  readonly GRAPH_NODE_STYLES = {
    user: {
      icon: 'user',
      color: 'dark',
    },
    data: {
      icon: 'bezier-curve',
      color: 'dark',
    },
    source: {
      icon: 'sign-out-alt', // fa-microchip
      color: 'source',
    },
    sink: {
      icon: 'sign-in-alt', // fa-microchip
      color: 'sink',
    },

    processor: {
      icon: 'microchip', // fa-microchip
      color: 'processor',
    },
    purpose: {
      icon: 'bullseye', // fa-bullseye
      color: 'purpose',
    },

    processing: {
      icon: 'cog', // fa-cog
      color: 'processing',
    },
    storage: {
      icon: 'database', // fa-database
      color: 'storage',
    },
    shared: {
      icon: 'share-alt', // fa-project-diagram
      color: 'shared',
    },
  };
}
