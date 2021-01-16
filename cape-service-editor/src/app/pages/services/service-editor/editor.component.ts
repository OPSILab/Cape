import { Component, OnInit } from '@angular/core';
import { JSONEditor } from '@json-editor/json-editor/dist/jsoneditor.js';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import * as $ from 'jquery';
import { NbDialogService } from '@nebular/theme';
import { DialogNamePromptComponent } from './dialog-name-prompt/dialog-name-prompt.component';
import { DialogImportPromptComponent } from './dialog-import-prompt/dialog-import-prompt.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AvailableServicesService } from '../availableServices/availableServices.service';
import { NgxConfigureService } from 'ngx-configure';
import { LoginService } from '../../../login/login.service';
import { ErrorDialogService } from '../../error-dialog/error-dialog.service';

//import { ToasterConfig } from 'angular2-toaster';
//import 'style-loader!angular2-toaster/toaster.css';
import {
  NbComponentStatus,
  NbGlobalLogicalPosition,
  NbGlobalPhysicalPosition,
  NbGlobalPosition,
  NbToastrService,
} from '@nebular/theme';



@Component({
  selector: 'ngx-spinner-color',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css']
})
export class EditorComponent implements OnInit {
  private doc: any;
  private editor: any;
  private sub: any;
  private serviceData: any;
  loading = false;
  public config: any;
  apiRoot: string;
  schemaDir;
  public onEdit=false;

  //configToaster: ToasterConfig;



  constructor(@Inject(DOCUMENT) document, private toastrService: NbToastrService, 
  private dialogService: NbDialogService, private router: Router, private route: ActivatedRoute, 
  private service: AvailableServicesService, configService: NgxConfigureService,private loginService: LoginService, private errorDialogService: ErrorDialogService ) {

    this.doc = document;
    this.config = configService.config;
    this.schemaDir = this.config.editorSchemaPath;
    this.loading = true;
  }




  ngOnDestroy() {
    this.sub.unsubscribe();
  }



  ngOnInit() {



    this.sub = this.route.params.subscribe(params => {


      async function getService(serviceId: string, service: any) {

        let service_data = await service.getService(serviceId);
        console.log(service_data);
        return service_data.json();

      }

      // this.loading = true;

      this.serviceData = {};

      if (params['serviceId']) {

        this.service.getService(params['serviceId']).subscribe(
          result => {
            console.log(result); this.serviceData = result; this.onEdit = true; this.initializeEditor(this.serviceData);
          }
        );

      } else {
        this.initializeEditor(this.serviceData);
      }
    });

  }


  initializeEditor(serviceData) {

    console.log("prima?");
    console.log(this.serviceData);
    const elem = this.doc.getElementById('editor');
    var starting_value = serviceData;
    //console.log(starting_value);

    var editor = new JSONEditor(elem, {
      ajax: true, schema: { $ref: this.schemaDir }, startval: starting_value, theme: 'bootstrap4'
      , iconlib: 'fontawesome5', no_additional_properties: true, disable_properties: true, prompt_before_delete: true, required_by_default: true
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
        console.log("field with path: [" + path + "] changed to [" + JSON.stringify(this.getEditor(path).getValue()) + "]");
        console.log(this.getEditor(path));
        if (value !== "\"undefined\"" && value !== "\"\"") {
          var e = $('select[name="' + this.getEditor(path).formname + '"]');
          var nameValue = e[0].options[e[0].selectedIndex].text;
          //console.log(path.substr(0, path.lastIndexOf(".") + 1) + ".name");
          this.getEditor(path.substr(0, path.lastIndexOf(".") + 1) + "name").setValue(nameValue);
        }
      }
      for (var key in editor.editors) {
        var regex = ".conceptId";

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
      $("nb-spinner").remove();

    });

  }


  closeSpinner() {
    console.log("closing");
    this.loading = false;
    $("nb-spinner").remove();
  }


  saveAsFile() {
    this.dialogService.open(DialogNamePromptComponent)
      .onClose.subscribe(name => name && this.saveFile(name));
  }


  importAsFile() {
    this.dialogService.open(DialogImportPromptComponent)
      .onClose.subscribe(name => name && this.importFile(name));
  }


  importFile(name) {
    console.log(name)

    this.editor.setValue(name);
  }


  saveFile(name) {
    var example = this.editor.getValue(),
      filename = name + '.json',
      blob = new Blob([JSON.stringify(example, null, 2)], {
        type: "application/json;charset=utf-8"
      });

    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
      window.navigator.msSaveOrOpenBlob(blob, filename);
    } else {
      var a = document.createElement('a');
      a.download = filename;
      a.href = URL.createObjectURL(blob);
      a.dataset.downloadurl = ['text/plain', a.download, a.href].join(':');

      a.dispatchEvent(new MouseEvent('click', {
        'view': window,
        'bubbles': true,
        'cancelable': false
      }));
    }
  }


  stopLoading() {
    console.log(this.loading)
    this.loading = false;
  }


  async save() {
    if (confirm('Service will be saved, proceed?')) {

      var payload = this.editor.getValue();
      
      try {
          await this.service.saveService(payload);
          this.showToast('success', "Success!", "Service " + payload.name + " saved.")
        } catch (error) {
          if (error.error?.statusCode === '401') {
            this.loginService.logout();
            this.router.navigate(['/login']);
          } else
            this.errorDialogService.openErrorDialog("Errors occurred in service registration. Please be careful and try again...");
        }     
    }
  }


  async update() {
    if (confirm('Service will be saved, proceed?')) {

      var payload = this.editor.getValue();
      
      try {
          await this.service.updateService(payload, payload.serviceId);
          this.showToast('success', "Success!", "Service " + payload.name + " updated.")
        } catch (error) {
          if (error.error?.statusCode === '401') {
            this.loginService.logout();
            this.router.navigate(['/login']);
          } else
            this.errorDialogService.openErrorDialog("Errors occurred in service registration. Please be careful and try again...");
        }  

      
      
    }
  }


  private showToast(type: NbComponentStatus, title: string, body: string) {
    const config = {
      status: type,
      destroyByClick: true,
      duration: 2000,
      hasIcon: true,
      position: NbGlobalPhysicalPosition.TOP_RIGHT,
      preventDuplicates: true,
    };
    const titleContent = title;


    this.toastrService.show(
      body,
      `${titleContent}`);
  }

}