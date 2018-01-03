import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

// annyang declaration
// https://github.com/TalAter/annyang
declare var annyang: any;

@Injectable()
export class SpeechService {

  wordsSpoken = new Subject<String>();
  constructor() { }

  init() {
    annyang.addCommands({});
    annyang.addCallback('result', (userSaid) => {
      console.log('User said:', userSaid);
      this.wordsSpoken.next(userSaid[0]);
    });
    this.startListening();
  }
  get speechSupportedByBrowser(): boolean {
    return !!annyang;
  }

  startListening() {
    // annyang.start();
    annyang.start({ autoRestart: true, continuous: false });
  }

  stopListening() {
    annyang.abort();
  }

  changeLanguage(lang: string) {
    annyang.setLanguage(lang);
  }
}
