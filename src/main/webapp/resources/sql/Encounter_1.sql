select `INSTITUTION_ID`, count(`INSTITUTION_ID`),`ENCOUNTERDATE`,`REFERALINSTITUTION_ID`
from encounter
where `RETIRED`=false and encounterType="Test_Enrollment"
and receivedAtLab is null  group by `INSTITUTION_ID`,`ENCOUNTERDATE`,`REFERALINSTITUTION_ID`;
select id,`INSTITUTION_ID`,`ENCOUNTERTYPE`, `ENCOUNTERDATE`, `REFERALINSTITUTION_ID`,`RECEIVEDATLAB`
from encounter
where `ID`=72764 or `ID`=72776;