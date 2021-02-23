/* eslint-disable @typescript-eslint/restrict-plus-operands */
/* eslint-disable @typescript-eslint/restrict-template-expressions */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-floating-promises */
/* eslint-disable @typescript-eslint/no-unsafe-return */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
import { Component, OnInit, Input } from '@angular/core';
import * as d3Tree from 'd3-mitch-tree';
import * as d3 from 'd3';
import { NbIconLibraries } from '@nebular/theme';
import { Globals } from '../../../globals';
import { Router } from '@angular/router';

@Component({
  selector: 'd3-tree',
  templateUrl: './d3-tree.component.html',
  styleUrls: ['./d3-tree.component.scss'],
})
export class D3treeComponent implements OnInit {
  legendIcons: unknown[];
  concepts: string[] = [''];
  @Input() data;
  boxedtree;

  constructor(private iconsLibrary: NbIconLibraries, private globals: Globals, private router: Router) {
    this.iconsLibrary.registerFontPack('fa', {
      packClass: 'fa',
      iconClassPrefix: 'fa',
    });
  }

  initData(): void {
    this.initTree(this.data);
  }

  ngOnInit(): void {
    this.initLegend();
    this.initData();
  }

  initLegend() {
    this.legendIcons = [
      {
        title: 'Data',
        class: this.globals.GRAPH_NODE_STYLES['data'].icon,
        color: this.globals.GRAPH_NODE_STYLES['data'].color,
      },
      {
        title: 'Processor ',
        class: this.globals.GRAPH_NODE_STYLES['processor'].icon,
        color: this.globals.GRAPH_NODE_STYLES['processor'].color,
      },
      {
        title: 'Processor (Data source)',
        class: this.globals.GRAPH_NODE_STYLES['source'].icon,
        color: this.globals.GRAPH_NODE_STYLES['source'].color,
      },
      {
        title: 'Processor (Data sink)',
        class: this.globals.GRAPH_NODE_STYLES['sink'].icon,
        color: this.globals.GRAPH_NODE_STYLES['sink'].color,
      },
      {
        title: 'Purpose',
        class: this.globals.GRAPH_NODE_STYLES['purpose'].icon,
        color: this.globals.GRAPH_NODE_STYLES['purpose'].color,
      },
      {
        title: 'Processing',
        class: this.globals.GRAPH_NODE_STYLES['processing'].icon,
        color: this.globals.GRAPH_NODE_STYLES['processing'].color,
      },
      {
        title: 'Storage',
        class: this.globals.GRAPH_NODE_STYLES['storage'].icon,
        color: this.globals.GRAPH_NODE_STYLES['storage'].color,
      },
      {
        title: 'Shared',
        class: this.globals.GRAPH_NODE_STYLES['shared'].icon,
        color: this.globals.GRAPH_NODE_STYLES['shared'].color,
      },
    ];
  }

  initTree(data): void {
    const options = {
      data: data,
      allowFocus: false,
      allowZoom: true,
      allowPan: true,
      allowNodeCentering: true,
      element: document.getElementById('visualisation'),
      getId: function (data) {
        return data.id;
      },
      getChildren: function (data) {
        return data.children;
      },
      getBodyDisplayText: function (data) {
        return data.description;
      },
      getTitleDisplayText: function (data) {
        return data.name;
      },
    };
    const self = this.router;

    this.boxedtree = new d3Tree.boxedTree(options)
      .on('nodeClick', function (event) {
        const node = event.nodeDataItem.data;

        if (
          node.type == 'source' ||
          node.type == 'sink' ||
          node.type == 'processor' ||
          node.type == 'processing' ||
          node.type == 'purpose' ||
          node.type == 'storage' ||
          node.type == 'shared'
        ) {
          event.continue = false;
          if (node.url)
            /*window.open(node.url, '_blank')*/
            self.navigate(['/pages/consents', { consentId: 1 }]);
        }
      })
      .initialize();
    const conceptsList = [];
    const nodes = this.boxedtree.getNodes();
    const boxedtree = this.boxedtree;
    nodes.forEach(function (node, index, arr) {
      //boxedtree.expand(node);

      if (
        node.data.type == 'source' ||
        node.data.type == 'sink' ||
        node.data.type == 'processor' ||
        node.data.type == 'processing' ||
        node.data.type == 'storage' ||
        node.data.type == 'purpose' ||
        node.data.type == 'shared'
      )
        boxedtree.expand(node);
      if (node.data.type == 'data') {
        conceptsList.push(node.data.description);
      }
    });

    boxedtree.update(boxedtree.getRoot());
    this.concepts = conceptsList;

    const nodeStyles = this.globals.GRAPH_NODE_STYLES;

    function getIconFromType(type) {
      return nodeStyles[type].icon;
    }

    function getTranslateValues(translate) {
      const parts = translate.split(',');
      const x = parts[0].split('(')[1];
      const y = parts[1].split(')')[0];

      return [x, y];
    }

    function updateTreeClasses(treePlugin) {
      const nodeElements = treePlugin.getPanningContainer().selectAll('g.node');

      nodeElements.each(function (data, index, elArr) {
        const nodeType = data.data.type;

        const textBox = elArr[index].querySelector('.body-group').querySelector('.d3plus-textBox');
        d3.select(textBox.childNodes[0]).attr('font-size', '16px').style('font-size', null);
        textBox.childNodes[0].innerHTML = data.data.description;

        const titleGroup = elArr[index].querySelector('.title-group');
        const title = titleGroup.querySelector('.d3plus-textBox');

        title.innerHTML = '';

        if (nodeType != 'root' && nodeType != 'data') {
          const box = titleGroup.querySelector('.title-box');

          d3.select(box).attr('width', 40).attr('height', 45);

          const translate = d3.select(titleGroup).attr('transform');
          const y = getTranslateValues(translate)[1];

          d3.select(titleGroup).attr('transform', `translate(-10, ${y})`);

          const icon = getIconFromType(nodeType);

          d3.select(title)
            .append('svg:foreignObject')
            .attr('color', 'white')
            .attr('width', 100)
            .attr('height', 100)
            .append('xhtml:span')
            .html(`<i class="fa fa-${icon} fa-2x"></i>`);
        } else {
          const icon = nodeType == 'root' ? getIconFromType('user') : getIconFromType('data');

          const x = {
            user: 65,
            'bezier-curve': 55,
          };

          d3.select(textBox).select('foreignObject').remove();

          d3.select(textBox)
            .append('svg:foreignObject')
            .attr('color', 'black')
            .attr('width', 100)
            .attr('height', 100)
            .attr('transform', `translate(${x[icon]}, -80)`)
            .append('xhtml:span')
            .html(`<i class="fa fa-${icon} fa-4x"></i>`);
        }
      });

      nodeElements.attr('class', function (data, index, arr) {
        const node = data.data;

        const nodeType = node.type.toLowerCase();

        const nodeClass = 'graph-node-' + nodeType;

        const existingClasses = this.getAttribute('class');
        if (!existingClasses) return nodeClass;

        const hasDepthClassAlready = (' ' + existingClasses + ' ').indexOf(' ' + nodeClass + ' ') > -1;

        if (hasDepthClassAlready) return existingClasses;

        return existingClasses + ' ' + nodeClass;
      });
    }

    // Override the core update method,
    // so it'd call our custom update method
    this.boxedtree.update = function (nodeDataItem) {
      // Call the original update method
      this.__proto__.update.call(this, nodeDataItem);
      updateTreeClasses(this);
    };

    updateTreeClasses(this.boxedtree);
  }

  filterNodes(e) {
    console.log(e);

    const nodeMatchingText = this.boxedtree.getNodes().find(function (node) {
      return node.data.description == e;
    });
    if (nodeMatchingText) {
      console.log('found');

      this.boxedtree.centerNode(nodeMatchingText);
    }
  }
}
