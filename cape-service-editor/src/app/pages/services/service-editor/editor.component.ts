/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
import { AfterContentInit, ChangeDetectorRef, Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { JSONEditor } from '@json-editor/json-editor/dist/jsoneditor.js';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import * as $ from 'jquery';
import { NbDialogService } from '@nebular/theme';
import { DialogNamePromptComponent } from './dialog-name-prompt/dialog-name-prompt.component';
import { DialogImportPromptComponent } from './dialog-import-prompt/dialog-import-prompt.component';
import { ActivatedRoute } from '@angular/router';
import { AvailableServicesService } from '../availableServices/availableServices.service';
import { NgxConfigureService } from 'ngx-configure';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';

import { NbComponentStatus, NbToastrService } from '@nebular/theme';
import { TranslateService } from '@ngx-translate/core';
import { AppConfig, System } from '../../../model/appConfig';
import { ServiceEntry } from '../../../model/service-linking/serviceEntry';

@Component({
  selector: 'ngx-spinner-color',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css'],
})
export class EditorComponent implements OnInit, AfterContentInit, OnDestroy {
  private editor: any;
  public serviceId: string;
  private serviceData: ServiceEntry;
  loading = false;
  public readOnly = false;
  private config: AppConfig;
  private systemConfig: System;
  apiRoot: string;
  schemaDir: string;
  public isNew = false;

  @ViewChild('confirmSaveDialog', { static: false }) confirmSaveDialogTemplate: TemplateRef<any>;
  @ViewChild('confirmUpdateDialog', { static: false }) confirmUpdateDialogTemplate: TemplateRef<any>;
  @ViewChild('confirmOverwriteDialog', { static: false }) confirmOverwriteDialogTemplate: TemplateRef<any>;

  constructor(
    @Inject(DOCUMENT) private document: Document,
    private toastrService: NbToastrService,
    private dialogService: NbDialogService,
    private route: ActivatedRoute,
    private availablesServicesService: AvailableServicesService,
    private configService: NgxConfigureService,
    private errorDialogService: ErrorDialogService,
    private translateService: TranslateService,
    private cdr: ChangeDetectorRef
  ) {
    this.config = this.configService.config as AppConfig;
    this.systemConfig = this.config.system;
    this.schemaDir =
      (this.systemConfig.serviceEditorUrl.includes('localhost') ? '' : this.systemConfig.serviceEditorUrl) + this.systemConfig.editorSchemaPath;
    this.loading = true;
  }

  ngAfterContentInit(): void {
    if (this.readOnly) sessionStorage.setItem('readOnly', 'true');
  }

  async ngOnInit(): Promise<void> {
    this.serviceId = this.route.snapshot.params['serviceId'] as string;
    this.readOnly = <boolean>this.route.snapshot.params['readOnly'];
    sessionStorage.removeItem('isTouched');
    if (this.serviceId) {
      this.serviceData = await this.availablesServicesService.getService(this.serviceId);
    } else {
      this.isNew = true;
    }

    this.initializeEditor(this.serviceData);
    // this.loading = true;
  }

  ngOnDestroy(): void {
    if (this.readOnly) sessionStorage.removeItem('readOnly');
    sessionStorage.removeItem('isTouched');
  }

  readSessionStorageValue(key: string): string {
    return sessionStorage.getItem(key);
  }

  initializeEditor(serviceData: ServiceEntry): void {
    const elem = this.document.getElementById('editor');

    const editor = new JSONEditor(elem, {
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

    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    this.editor = editor;
    let isFirstChange = true;
    // Hook up the validation indicator to update its status whenever the editor changes
    editor.on('change', function () {
      if (!isFirstChange) sessionStorage.setItem('isTouched', 'true');
      else isFirstChange = false;
      // Get an array of errors from the validator
      // const errors = editor.validate();
      // const indicator = document.getElementById('valid_indicator');
      // watcher on concepts fields
      const watcherCallback = function (path) {
        const value = JSON.stringify(this.getEditor(path).getValue() as Record<string, unknown>);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        console.log(`field with path: [${path as string}] changed to [${JSON.stringify(this.getEditor(path).getValue())}]`);

        if (value !== '"undefined"' && value !== '""') {
          // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
          const e = $('select[name="' + this.getEditor(path).formname + '"]');
          const nameValue = e[0].options[e[0].selectedIndex].text;
          //console.log(path.substr(0, path.lastIndexOf(".") + 1) + ".name");
          // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
          this.getEditor(path.substr(0, path.lastIndexOf('.') + 1) + 'name').setValue(nameValue);
        }
      };
      for (const key in editor.editors) {
        const regex = '.conceptId';

        if (Object.prototype.hasOwnProperty.call(editor.editors, key) && RegExp(regex).exec(key)) {
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
      if (sessionStorage.getItem('readOnly') === 'true') editor.disable();
    });
  }

  closeSpinner(): void {
    console.log('closing');
    this.loading = false;
    $('nb-spinner').remove();
  }

  saveAsFile(): void {
    this.dialogService.open(DialogNamePromptComponent).onClose.subscribe((name) => name && this.saveFile(name));
  }

  importAsFile(): void {
    this.dialogService.open(DialogImportPromptComponent).onClose.subscribe((jsonContent) => {
      if (jsonContent) {
        this.editor.setValue(jsonContent);
        this.editor.getEditor('root.serviceInstance.cert.x5u').disable();
        this.editor.getEditor('root.serviceInstance.cert.x5c').disable();
        this.serviceId = jsonContent.serviceId as string;
      }
    });
  }

  saveFile(name: string): void {
    this.errorDialogService;
    const example = this.editor.getValue() as Record<string, unknown>,
      filename = `${name}.json`,
      blob = new Blob([JSON.stringify(example, null, 2)], {
        type: 'application/json;charset=utf-8',
      });

    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
      window.navigator.msSaveOrOpenBlob(blob, filename);
    } else {
      const a = document.createElement('a');
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

  stopLoading(): void {
    console.log(this.loading);
    this.loading = false;
  }

  openSaveToRegistryDialog(): void {
    const payload = this.editor.getValue() as ServiceEntry;
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    const validationErrors = this.editor.validate();

    if (validationErrors.length > 0)
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      this.errorDialogService.openErrorDialog({ error: 'EDITOR_VALIDATION_ERROR', validationErrors: validationErrors });
    else {
      const ref = this.dialogService.open(this.confirmSaveDialogTemplate, {
        context: {
          name: payload.name,
          callback: async () => {
            try {
              await this.availablesServicesService.saveService(payload);
              this.isNew = false;
              this.showToast(
                'success',
                this.translateService.instant('general.editor.save_success', {
                  name: payload.name,
                }),
                ''
              );
              this.isNew = false;
              sessionStorage.removeItem('isTouched');
              ref.close();
            } catch (error) {
              ref.close();
              if (error.error.error === 'org.springframework.dao.DuplicateKeyException') this.openOverwriteToRegistryDialog();
              else this.errorDialogService.openErrorDialog(error);
            }
          },
        },
      });
    }
  }

  openUpdateToRegistryDialog(): void {
    const payload = this.editor.getValue() as ServiceEntry;
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    const validationErrors = this.editor.validate();

    if (validationErrors.length > 0)
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      this.errorDialogService.openErrorDialog({ error: 'EDITOR_VALIDATION_ERROR', validationErrors: validationErrors });
    else {
      const ref = this.dialogService.open(this.confirmUpdateDialogTemplate, {
        context: {
          name: payload.name,
          callback: async () => {
            try {
              await this.availablesServicesService.updateService(payload, payload.serviceId);
              sessionStorage.removeItem('isTouched');
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
  }

  openOverwriteToRegistryDialog(): void {
    const payload = this.editor.getValue() as ServiceEntry;
    const ref = this.dialogService.open(this.confirmOverwriteDialogTemplate, {
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
            this.isNew = false;
            sessionStorage.removeItem('isTouched');
          } catch (error) {
            this.errorDialogService.openErrorDialog(error);
          }
        },
      },
    });
  }

  private showToast(type: NbComponentStatus, title: string, body: string) {
    // const config = {
    //   status: type,
    //   destroyByClick: true,
    //   duration: 2500,
    //   hasIcon: false,
    //   position: NbGlobalPhysicalPosition.BOTTOM_RIGHT,
    //   preventDuplicates: true,
    // } as Partial<NbToastrConfig>;

    this.toastrService.show(body, title);
  }
}
