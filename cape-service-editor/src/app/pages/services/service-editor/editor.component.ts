import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { JSONEditor } from '@json-editor/json-editor/dist/jsoneditor.js';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import * as $ from 'jquery';
import { NbDialogService, NbToastrConfig } from '@nebular/theme';
import { DialogNamePromptComponent } from './dialog-name-prompt/dialog-name-prompt.component';
import { DialogImportPromptComponent } from './dialog-import-prompt/dialog-import-prompt.component';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AvailableServicesService } from '../availableServices/availableServices.service';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../../../login/login.service';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';

//import { ToasterConfig } from 'angular2-toaster';
//import 'style-loader!angular2-toaster/toaster.css';
import { NbComponentStatus, NbGlobalLogicalPosition, NbGlobalPhysicalPosition, NbGlobalPosition, NbToastrService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { AppConfig, System } from '../../../model/appConfig';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';

@Component({
  selector: 'ngx-spinner-color',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css'],
})
export class EditorComponent implements OnInit {
  private doc: any;
  private editor: any;
  private serviceId: string;
  private serviceData: ServiceEntry = {};
  loading = false;
  private config: AppConfig;
  private systemConfig: System;
  apiRoot: string;
  schemaDir: string;
  public onEdit = false;

  @ViewChild('confirmSaveDialog', { static: false }) confirmSaveDialogTemplate: TemplateRef<any>;
  @ViewChild('confirmUpdateDialog', { static: false }) confirmUpdateDialogTemplate: TemplateRef<any>;

  constructor(
    @Inject(DOCUMENT) document,
    private toastrService: NbToastrService,
    private dialogService: NbDialogService,
    private route: ActivatedRoute,
    private availablesServicesService: AvailableServicesService,
    configService: NgxConfigureService,
    private loginService: LoginService,
    private errorDialogService: ErrorDialogService,
    private translateService: TranslateService
  ) {
    this.doc = document;
    this.config = configService.config as AppConfig;
    this.systemConfig = this.config.system;
    this.schemaDir =
      (this.systemConfig.serviceEditorUrl.includes('localhost') ? '' : this.systemConfig.serviceEditorUrl) + this.systemConfig.editorSchemaPath;
    this.loading = true;
  }

  ngOnDestroy() {}

  async ngOnInit() {
    this.serviceId = this.route.snapshot.params['serviceId'];

    if (this.serviceId) {
      this.serviceData = await this.availablesServicesService.getService(this.serviceId);
      this.onEdit = true;
    }

    this.initializeEditor(this.serviceData);
    // this.loading = true;
  }

  initializeEditor(serviceData: ServiceEntry) {
    const elem = this.doc.getElementById('editor');

    var editor = new JSONEditor(elem, {
      ajax: true,
      schema: { $ref: this.schemaDir },
      startval: serviceData,
      theme: 'bootstrap4',
      iconlib: 'fontawesome5',
      no_additional_properties: true,
      disable_properties: true,
      prompt_before_delete: true,
      required_by_default: true,
    });

    this.editor = editor;

    // Hook up the validation indicator to update its status whenever the editor changes
    editor.on('change', function () {
      // Get an array of errors from the validator
      var errors = editor.validate();
      var indicator = document.getElementById('valid_indicator');
      // watcher on concepts fields
      var watcherCallback = function (path) {
        let value = JSON.stringify(this.getEditor(path).getValue());
        console.log('field with path: [' + path + '] changed to [' + JSON.stringify(this.getEditor(path).getValue()) + ']');
        console.log(this.getEditor(path));
        if (value !== '"undefined"' && value !== '""') {
          var e = $('select[name="' + this.getEditor(path).formname + '"]');
          var nameValue = e[0].options[e[0].selectedIndex].text;
          //console.log(path.substr(0, path.lastIndexOf(".") + 1) + ".name");
          this.getEditor(path.substr(0, path.lastIndexOf('.') + 1) + 'name').setValue(nameValue);
        }
      };
      for (var key in editor.editors) {
        var regex = '.conceptId';

        if (editor.editors.hasOwnProperty(key) && key.match(regex)) {
          editor.watch(key, watcherCallback.bind(editor, key));
        }
      }
    });

    //editor.on('ready', this.closeSpinner);

    editor.on('ready', function () {
      editor.getEditor('root.serviceInstance.cert.x5u').disable();
      editor.getEditor('root.serviceInstance.cert.x5c').disable();
      this.loading = false;
      $('nb-spinner').remove();
    });
  }

  closeSpinner() {
    console.log('closing');
    this.loading = false;
    $('nb-spinner').remove();
  }

  saveAsFile() {
    this.dialogService.open(DialogNamePromptComponent).onClose.subscribe((name) => name && this.saveFile(name));
  }

  importAsFile() {
    this.dialogService.open(DialogImportPromptComponent).onClose.subscribe((name) => name && this.importFile(name));
  }

  importFile(name) {
    console.log(name);

    this.editor.setValue(name);
  }

  saveFile(name) {
    var example = this.editor.getValue(),
      filename = name + '.json',
      blob = new Blob([JSON.stringify(example, null, 2)], {
        type: 'application/json;charset=utf-8',
      });

    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
      window.navigator.msSaveOrOpenBlob(blob, filename);
    } else {
      var a = document.createElement('a');
      a.download = filename;
      a.href = URL.createObjectURL(blob);
      a.dataset.downloadurl = ['text/plain', a.download, a.href].join(':');

      a.dispatchEvent(
        new MouseEvent('click', {
          view: window,
          bubbles: true,
          cancelable: false,
        })
      );
    }
  }

  stopLoading() {
    console.log(this.loading);
    this.loading = false;
  }

  openSaveToRegistryDialog() {
    const payload = this.editor.getValue();

    const ref = this.dialogService.open(this.confirmSaveDialogTemplate, {
      context: {
        name: payload.name,
        callback: async () => {
          try {
            await this.availablesServicesService.saveService(payload);
            this.showToast(
              'success',

              this.translateService.instant('general.editor.save_success', {
                name: payload.name,
              }),
              ''
            );
            ref.close();
          } catch (error) {
            this.errorDialogService.openErrorDialog(error);
          }
        },
      },
    });
  }

  openUpdateToRegistryDialog() {
    const payload = this.editor.getValue();
    const ref = this.dialogService.open(this.confirmUpdateDialogTemplate, {
      context: {
        name: payload.name,
        callback: async () => {
          try {
            await this.availablesServicesService.updateService(payload, payload.serviceId);
            this.showToast(
              'success',

              this.translateService.instant('general.editor.update_success', {
                name: payload.name,
              }),
              ''
            );
            ref.close();
          } catch (error) {
            this.errorDialogService.openErrorDialog(error);
          }
        },
      },
    });
  }

  private showToast(type: NbComponentStatus, title: string, body: string) {
    const config = {
      status: type,
      destroyByClick: true,
      duration: 2500,
      hasIcon: false,
      position: NbGlobalPhysicalPosition.BOTTOM_RIGHT,
      preventDuplicates: true,
    } as Partial<NbToastrConfig>;

    this.toastrService.show(body, title);
  }
}
