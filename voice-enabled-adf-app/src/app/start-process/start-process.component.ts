import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProcessInstance, StartProcessInstanceComponent } from '@alfresco/adf-process-services';
import { SpeechService } from './../services/speech.service';
import { FormService, FormEvent, FormModel, StartFormComponent } from '@alfresco/adf-core';
import { MatSlideToggleModule, MatSelectChange } from '@angular/material';
import { OnChanges, OnDestroy } from '@angular/core/src/metadata/lifecycle_hooks';

@Component({
  selector: 'app-start-process',
  templateUrl: './start-process.component.html',
  styleUrls: ['./start-process.component.scss']
})
export class StartProcessComponent implements OnInit, OnDestroy {

  appId: string = null;
  fieldId: string = null;
  srcElement: any;
  form: FormModel;
  voiceText: string;
  checked: boolean;
  isFormAvailable: boolean;
  isSpeechSupportedByBrowser: boolean;
  // If looking for a language outside of this list, check out language-codes.txt in this directory!
  languages = [
    { value: 'en-US', viewValue: 'English US' },
    { value: 'en-US', viewValue: 'English Australia' },
    { value: 'en-US', viewValue: 'English United Kingdom' },
    { value: 'es-ES', viewValue: 'Español' },
    { value: 'fr-FR', viewValue: 'Français' },
    { value: 'de-DE', viewValue: 'German' },
    { value: 'it-IT', viewValue: 'Italiano' },
    { value: 'ml-IN', viewValue: 'Malayalam' },
    { value: 'pt-PT', viewValue: 'Português' }
  ];


  constructor(private router: Router,
    private route: ActivatedRoute,
    public speech: SpeechService,
    formService: FormService) {
    this.isSpeechSupportedByBrowser = this.speech.speechSupportedByBrowser;
    if (this.isSpeechSupportedByBrowser) {

      this.voiceText = 'Enable Voice!';
      this.checked = false;
      formService.formLoaded.subscribe(
        (e: FormEvent) => {
          if (e.form) {
            this.isFormAvailable = true;
            this.form = e.form;
          } else {
            if (this.checked) {
              this.speech.stopListening();
            }
          }
        }
      );
      formService.formEvents.subscribe(
        (e: Event) => {
          if (e.type === 'focusin') {
            this.fieldId = e.srcElement['id'];
            this.srcElement = e.srcElement;
          }
        }
      );
      this.speech.wordsSpoken.subscribe(word => {
        this.srcElement['value'] = this.srcElement['value'] + ' ' + word;
        this.form.getFieldById(this.fieldId).value = this.srcElement['value'];
      });
    }
  }

  ngOnInit() {

    this.route.params.subscribe(params => {
      if (params.appId && params.appId !== '0') {
        this.appId = params.appId;
      } else {
        this.router.navigate(['/apps']);
      }
    });
  }
  ngOnDestroy() {
    if (this.isSpeechSupportedByBrowser) {
      this.speech.stopListening();
    }
  }

  onProcessStarted(process: ProcessInstance) {
    this.router.navigate(['/apps', this.appId || 0, 'tasks']);
  }

  onCancelStartProcess() {
    this.router.navigate(['/apps', this.appId || 0, 'tasks']);
  }

  onToggle() {
    this.checked = !this.checked;
    if (this.checked) {
      this.speech.init();
      this.voiceText = 'Disable Voice!';
    } else {
      this.voiceText = 'Enable Voice!';
      this.speech.stopListening();
    }
  }

  onLanguageChange(event: MatSelectChange) {
    this.speech.changeLanguage(event.value);
  }
}
