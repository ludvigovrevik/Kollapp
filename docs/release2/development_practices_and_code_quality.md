# 📄 Dokumentasjon av Arbeidsvaner, Arbeidsflyt og Kodekvalitet

I dette prosjektet har vi tatt bevisste valg for å sikre en effektiv arbeidsflyt, gode arbeidsvaner og høy kodekvalitet. Denne dokumentasjonen beskriver vår tilnærming til versjonskontroll, samarbeid og testing, samt hvilke verktøy vi har brukt for å opprettholde kodekvaliteten.

---

## 🛠️ Arbeidsvaner

- **Parprogrammering og Selvstendig Arbeid**:
  Vi har kombinert parprogrammering og selvstendig arbeid for å dra nytte av gruppens kollektive kunnskap samtidig som vi opprettholder produktiviteten når vi jobber individuelt. Parprogrammering har vært særlig nyttig for å løse komplekse problemer, diskutere funksjonaliteter, og identifisere bugs tidlig i prosessen.

- **Regelmessige Møter**:
  Gruppen har hatt tre faste møter per uke der vi har diskutert prosjektets status, adressert eventuelle utfordringer, og fordelt oppgaver. Dette har sikret god koordinering, fremdrift, og kontinuerlig kommunikasjon blant alle medlemmene.

- **Oppgavefordeling og Brukerhistorier**:
  Vi har delt opp oppgaver basert på brukerhistorier som er definert på forhånd. Dette har gitt oss et klart bilde av hva som skal prioriteres og hvilke funksjonaliteter som skal implementeres først, noe som gjør utviklingsprosessen mer fokusert og strukturert.

---

## 🔄 Arbeidsflyt

- **Versjonskontrollsystem og Branch-struktur**:
  Vi bruker Git som vårt versjonskontrollsystem med en strukturert branch-struktur for å sikre stabilitet i hovedkoden (`master`-branch). Når vi jobber på ulike issues som er knyttet til de forhåndsdefinerte brukerhistoriene, oppretter vi alltid egne feature-branches. Dette hindrer konflikter med `master`-branchen og gir oss muligheten til å teste nye funksjoner uten å påvirke hovedkoden.

- **Merge Requests og Kodegjennomgang**:
  Før en merge request sendes til `master`, sørger vi alltid for at en annen person i gruppen ser gjennom og godkjenner forespørselen. Denne praksisen sikrer at endringene som legges inn i hovedkoden er av høy kvalitet og følger våre kodestandarder. Kodegjennomgang bidrar også til kunnskapsdeling i gruppen og til å oppdage eventuelle problemer tidlig.

---

## ⚙️ Kodekvalitet og Testing

- **Tilnærming til Testing**:
  Selv om vi utviklet testene mot slutten av prosjektet, la vi stor vekt på grundig testing av alle kritiske funksjoner. Vi brukte både enhetstester og integrasjonstester for å sikre at applikasjonen fungerer som forventet under ulike scenarier.

- **Verktøy for Kodekvalitet**:
  Vi har brukt en rekke verktøy for å opprettholde og forbedre kodekvaliteten:
  - **JaCoCo**: For å måle testdekningen og sikre at vi tester en stor del av koden.
  - **Mockito**: For mocking av avhengigheter i enhetstester, slik at vi kan isolere spesifikke funksjoner.
  - **Checkstyle**: For å følge kodestandarder og opprettholde konsistens i koden.
  - **SpotBugs**: For å identifisere potensielle feil og svakheter i koden, slik at vi kan adressere dem før de blir problemer i produksjon.

- **Innstillinger og Konfigurasjon**:
  - **JaCoCo** er konfigurert til å generere detaljerte rapporter om testdekningen etter hver bygging av prosjektet.
  - **Checkstyle** er satt opp med regler som følger standard Java-kodekonvensjoner for å gjøre koden mer lesbar og vedlikeholdbar.
  - **SpotBugs** er integrert i vår CI-pipeline, slik at den automatisk kjører og analyserer koden ved hver pull request.

---

## 🤝 Samarbeid og Prosessforbedring

- Vi har jobbet i korte iterasjoner (sprints), som har gitt oss muligheten til å levere forbedringer kontinuerlig og raskt tilpasse oss til eventuelle endringer i prosjektet.
- Etter hver iterasjon har vi hatt retrospektive møter for å evaluere hva som fungerte godt, og hva som kunne forbedres i vår arbeidsflyt.
- Alle fremgang og beslutninger har blitt grundig dokumentert for å sikre at prosjektet har vært godt organisert og gjennomsiktig.

---

## 📈 Oppsummering

Vår strukturerte arbeidsflyt, sammen med bruken av effektive verktøy som **JaCoCo**, **Mockito**, **Checkstyle**, og **SpotBugs**, har gjort det mulig for oss å opprettholde høy kodekvalitet og effektivitet i utviklingsprosessen. Vår tilnærming til bruk av branches og kodegjennomganger har bidratt til et stabilt og velfungerende prosjekt som alle gruppemedlemmene har hatt nytte av.

