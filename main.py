import datetime
import sys
import random
from threading import Thread
from PyQt5 import QtCore
from PyQt5.QtCore import Qt
from PyQt5.QtGui import QMovie
from PyQt5.QtWidgets import QApplication, QMainWindow, QDesktopWidget, QDialog, QMessageBox, QGraphicsBlurEffect
import BorrowFrame
import BrokenFrame
import ReturnFrame
import MainFrame
from serial import Serial
import JsonFirestore
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

# Declare app
app = QApplication(sys.argv)
Json = JsonFirestore

# Use a service account
cred = credentials.Certificate('serviceAccountKey.json')
firebase_admin.initialize_app(cred)
db = firestore.client()

period = [["00-00", "06-45"],
          ["06-45", "07-30"],
          ["07-30", "08-15"],
          ["08-25", "09-10"],
          ["09-20", "10-15"],
          ["10-15", "11-00"],
          ["11-00", "11-45"],
          ["12-30", "13-15"],
          ["13-15", "14-00"],
          ["14-10", "14-55"],
          ["15-05", "15-50"],
          ["16-00", "16-45"],
          ["16-45", "17-30"],
          ["17-45", "18-30"],
          ["18-30", "19-15"]]

translateDate = {"Thứ hai": "Monday",
                 "Thứ ba": "Tuesday",
                 "Thứ tư": "Wednesday",
                 "Thứ năm": "Thursday",
                 "Thứ sáu": "Friday",
                 "Thứ bảy": "Saturday",
                 "Chủ nhật": "Sunday",
                 "Monday": "Monday",
                 "Tuesday": "Tuesday",
                 "Wednesday": "Wednesday",
                 "Thursday": "Thursday",
                 "Friday": "Friday",
                 "Saturday": "Saturday",
                 "Sunday": "Sunday"}

VieDate = {"Thứ hai": "Thứ Hai",
           "Thứ ba": "Thứ ba",
           "Thứ tư": "Thứ tư",
           "Thứ năm": "Thứ năm",
           "Thứ sáu": "Thứ sáu",
           "Thứ bảy": "Thứ bảy",
           "Chủ nhật": "Chủ nhật",
           "Monday": "Thứ hai",
           "Tuesday": "Thứ ba",
           "Wednesday": "Thứ tư",
           "Thursday": "Thứ năm",
           "Friday": "Thứ sáu",
           "Saturday": "Thứ bảy",
           "Sunday": "Chủ nhật"}


class SerialThread(QtCore.QThread):
    dataChanged = QtCore.pyqtSignal(str)

    def run(self):
        while True:
            try:
                ser = Serial(port='/dev/ttyUSB0', baudrate=115200)
                rfid = str(ser.readline())
                if rfid != "b''":
                    rfid = rfid.split(":  ")[1].split("\\")[0]
                    print(rfid)
                    self.dataChanged.emit(rfid)
            except:
                continue


class BrokenF(BrokenFrame.Ui_Dialog):
    def setupUi(self, Dialog):
        super().setupUi(Dialog)
        # Appear position
        size = Dialog.size()
        desktopSize = QDesktopWidget().screenGeometry()
        top = int((desktopSize.height() / 2) - (size.height() / 2))
        left = int((desktopSize.width() / 2) - (size.width() / 2))
        Dialog.move(left, top)

        self.done.setVisible(False)

        self.del_remote.setVisible(False)
        self.del_hdmi.setVisible(False)
        self.del_laser.setVisible(False)
        self.del_micro.setVisible(False)
        self.del_remote.setEnabled(False)
        self.del_hdmi.setEnabled(False)
        self.del_laser.setEnabled(False)
        self.del_micro.setEnabled(False)
        # Function
        self.del_remote.clicked.connect(self.hideRemote)
        self.del_hdmi.clicked.connect(self.hideHDMI)
        self.del_laser.clicked.connect(self.hideLaser)
        self.del_micro.clicked.connect(self.hideMicro)

    def hideRemote(self):
        self.remote_room.setText("")
        self.del_remote.setEnabled(False)
        self.del_remote.setVisible(False)

    def hideHDMI(self):
        self.hdmi_room.setText("")
        self.del_hdmi.setEnabled(False)
        self.del_hdmi.setVisible(False)

    def hideLaser(self):
        self.laser_room.setText("")
        self.del_laser.setEnabled(False)
        self.del_laser.setVisible(False)

    def hideMicro(self):
        self.micro_room.setText("")
        self.del_micro.setEnabled(False)
        self.del_micro.setVisible(False)


class ReturnF(ReturnFrame.Ui_Dialog):
    def setupUi(self, Dialog):
        super().setupUi(Dialog)
        # Appear position
        size = Dialog.size()
        desktopSize = QDesktopWidget().screenGeometry()
        top = int((desktopSize.height() / 2) - (size.height() / 2))
        left = int((desktopSize.width() / 2) - (size.width() / 2))
        Dialog.move(left, top)
        # Setup date
        today = VieDate[datetime.datetime.now().strftime("%A")]
        date = datetime.datetime.now().strftime(today + ", ngày %d tháng %m")
        self.date.setText(date)


class BorrowF(BorrowFrame.Ui_Dialog):
    def setupUi(self, Dialog):
        super().setupUi(Dialog)
        # Appear position
        size = Dialog.size()
        desktopSize = QDesktopWidget().screenGeometry()
        top = int((desktopSize.height() / 2) - (size.height() / 2))
        left = int((desktopSize.width() / 2) - (size.width() / 2))
        Dialog.move(left, top)
        # Setup date
        today = VieDate[datetime.datetime.now().strftime("%A")]
        date = datetime.datetime.now().strftime(today + ", ngày %d tháng %m")
        self.date.setText(date)

        self.label.setVisible(False)
        # Offset
        self.del_remote.setVisible(False)
        self.del_hdmi.setVisible(False)
        self.del_laser.setVisible(False)
        self.del_micro.setVisible(False)
        self.del_remote.setEnabled(False)
        self.del_hdmi.setEnabled(False)
        self.del_laser.setEnabled(False)
        self.del_micro.setEnabled(False)
        # Function
        self.del_remote.clicked.connect(self.hideRemote)
        self.del_hdmi.clicked.connect(self.hideHDMI)
        self.del_laser.clicked.connect(self.hideLaser)
        self.del_micro.clicked.connect(self.hideMicro)

    def hideRemote(self):
        self.remote_room.setText("")
        self.del_remote.setEnabled(False)
        self.del_remote.setVisible(False)

    def hideHDMI(self):
        self.hdmi_room.setText("")
        self.del_hdmi.setEnabled(False)
        self.del_hdmi.setVisible(False)

    def hideLaser(self):
        self.laser_room.setText("")
        self.del_laser.setEnabled(False)
        self.del_laser.setVisible(False)

    def hideMicro(self):
        self.micro_room.setText("")
        self.del_micro.setEnabled(False)
        self.del_micro.setVisible(False)


class MainF(MainFrame.Ui_MainWindow):
    def __init__(self):
        self.serialThread = SerialThread()
        self.numberBroken = 0
        self.numberB = 0
        self.numberR = 0

    def setupUi(self, MainWindow):
        super().setupUi(MainWindow)
        # setup interface
        self.success.setVisible(False)
        GIF = QMovie("Images/animation.gif")
        self.label.setMovie(GIF)
        GIF.start()
        # Appear position
        size = MainWindow.size()
        desktopSize = QDesktopWidget().screenGeometry()
        top = int((desktopSize.height() / 2) - (size.height() / 2))
        left = int((desktopSize.width() / 2) - (size.width() / 2))
        MainWindow.move(left, top)

        # timeThread
        # self.timeThread.start()  # ------------------------------------------------------------------------------------
        today = VieDate[datetime.datetime.now().strftime("%A")]
        self.today.setText(today)
        date = datetime.datetime.now().strftime("ngày %d tháng %m năm 2022")
        self.date.setText(date)
        # Serial Thread
        self.serialThread.start()
        self.serialThread.dataChanged.connect(self.process)
        # Time to close Dialog
        # timeClose = Thread(target=self.CloseDialog)
        # timeClose.start()

    def process(self, rfid):
        try:
            if JsonFirestore.loadRFIDname(rfid) != None:
                self.BrokenDialog = QDialog()
                self.BrokenFr = BrokenF()
                self.BrokenFr.setupUi(self.BrokenDialog)

                self.BorrowDialog = QDialog()
                self.BorrowFr = BorrowF()
                self.BorrowFr.setupUi(self.BorrowDialog)

                self.ReturnDialog = QDialog()
                self.ReturnFr = ReturnF()
                self.ReturnFr.setupUi(self.ReturnDialog)

                self.name = JsonFirestore.loadRFIDname(rfid)
                self.mail = JsonFirestore.loadRFIDmail(rfid)

                self.RFID = rfid
                self.Last = db.collection("History").document(self.mail) \
                    .collection("EquipmentState").document("Last").get().to_dict()
                print(str(self.Last))
                if self.Last["LastState"] == "Borrowed":
                    self.setupRdialog()
                else:
                    if self.Last["LastState"] == "Returned":
                        self.setupBdialog()
        except:
            try:
                if JsonFirestore.loadDeviceID(rfid) != None:
                    deviceID = str(JsonFirestore.loadDeviceID(rfid))
                    self.room = deviceID[:3]
                    if self.numberB == 1 and self.numberBroken == 0:
                        if deviceID[3] == "1":
                            self.BorrowFr.del_remote.setEnabled(True)
                            self.BorrowFr.del_remote.setVisible(True)
                            self.BorrowFr.remote_room.setText(deviceID[:3])
                        if deviceID[3] == "2":
                            self.BorrowFr.del_hdmi.setEnabled(True)
                            self.BorrowFr.del_hdmi.setVisible(True)
                            self.BorrowFr.hdmi_room.setText(deviceID[:3])
                        if deviceID[3] == "3":
                            self.BorrowFr.del_laser.setEnabled(True)
                            self.BorrowFr.del_laser.setVisible(True)
                            self.BorrowFr.laser_room.setText(deviceID[:3])
                        if deviceID[3] == "4":
                            self.BorrowFr.del_micro.setEnabled(True)
                            self.BorrowFr.del_micro.setVisible(True)
                            self.BorrowFr.micro_room.setText(deviceID[:3])
                    if self.numberBroken == 1:
                        if deviceID[3] == "1":
                            self.BrokenFr.del_remote.setEnabled(True)
                            self.BrokenFr.del_remote.setVisible(True)
                            self.BrokenFr.remote_room.setText(deviceID[:3])
                        if deviceID[3] == "2":
                            self.BrokenFr.del_hdmi.setEnabled(True)
                            self.BrokenFr.del_hdmi.setVisible(True)
                            self.BrokenFr.hdmi_room.setText(deviceID[:3])
                        if deviceID[3] == "3":
                            self.BrokenFr.del_laser.setEnabled(True)
                            self.BrokenFr.del_laser.setVisible(True)
                            self.BrokenFr.laser_room.setText(deviceID[:3])
                        if deviceID[3] == "4":
                            self.BrokenFr.del_micro.setEnabled(True)
                            self.BrokenFr.del_micro.setVisible(True)
                            self.BrokenFr.micro_room.setText(deviceID[:3])
            except:
                pass

    def setupRdialog(self):
        self.ReturnFr.refirm.setText("")
        self.ReturnFr.name.setText(self.name)
        self.ReturnFr.email.setText("Email: " + self.mail.replace("_", ".").replace(".sis", "@sis"))
        # setup interface
        note = ""
        if str(self.Last["Bits_AHLM"])[0] == "1":
            note = note + "     01 ĐIỀU KHIỂN" + "\n"
            self.ReturnFr.remote_cb.setChecked(True)
        if str(self.Last["Bits_AHLM"])[2] == "1":
            note = note + "     01 BÚT LASER" + "\n"
            self.ReturnFr.laser_cb.setChecked(True)
        if str(self.Last["Bits_AHLM"])[3] == "1":
            note = note + "     01 MÍC DI ĐỘNG" + "\n"
            self.ReturnFr.micro_cb.setChecked(True)
        if str(self.Last["Bits_AHLM"])[1] == "1":
            note = note + "     01 CÁP HDMI" + "\n"
            self.ReturnFr.hdmi_cb.setChecked(True)
        note = note + self.reformTime(str(self.Last["LastCheck"]))

        re_minute = int(datetime.datetime.now().strftime("%M"))
        re_hour = int(datetime.datetime.now().strftime("%H"))
        i_minute = re_minute + 30 if re_minute < 30 else re_minute - 30
        i_hour = re_hour if re_minute < 30 else re_hour + 1
        str_minute = str(i_minute) if i_minute > 9 else f"0{i_minute}"
        str_hour = str(i_hour) if i_hour > 9 else f"0{i_hour}"
        timenow = str_hour + "-" + str_minute
        today = str(translateDate[str(datetime.datetime.now().strftime("%A"))])
        print(today)
        sections = JsonFirestore.readScheToday(self.RFID)
        check = 0
        for section in sections:
            section = JsonFirestore.readScheToday(self.RFID)[section]
            start = str(section["period"]).split("-")[0]
            end = str(section["period"]).split("-")[1]
            if (period[int(start)][0]) < timenow:
                if (period[int(end)][1]) > timenow:
                    check = 1
                    room = section["room"]
                    subject = section["subject"]
                    self.ReturnFr.room.setText("Phòng " + room + " - D6")
                    self.ReturnFr.subject.setText(subject)
                    self.ReturnFr.code.setText(str(random.randrange(100000, 999999)))
                    self.BorrowFr.label_.setText("|")
                    self.ReturnFr.start.setText(period[int(start)][0])
                    self.ReturnFr.end.setText(period[int(end)][1])
        if check == 0:
            self.ReturnFr.start.setText("")
            self.ReturnFr.end.setText("")
            self.ReturnFr.label_.setText("")
            self.ReturnFr.subject.setText("")
            self.ReturnFr.code.setText("")
            self.ReturnFr.room.setText("                  ...")

        self.BorrowFr.refirm.setFontPointSize(9)
        self.ReturnFr.refirm.setText(note)
        self.ReturnFr.back.clicked.connect(self.ReturnDialog_close)
        self.ReturnFr.confirm.clicked.connect(self.Return)
        self.ReturnFr.continue_.clicked.connect(self.resetRemainTime)
        self.ReturnFr.broken.clicked.connect(self.showBroken)

        self.resetRemainTime()
        GIF_ = QMovie("Images/countdown.gif")
        self.ReturnFr.remainTime.setMovie(GIF_)
        GIF_.start()
        # self.ReturnDialog.showFullScreen()
        self.ReturnDialog.show()
        self.timeShow = datetime.datetime.now().strftime("%M-%S")
        QtCore.QTimer.singleShot(29500, self.closeDialog)
        self.numberR = 1

    def setupBdialog(self):
        today = VieDate[datetime.datetime.now().strftime("%A")]
        self.BorrowFr.today.setText(today)
        date = datetime.datetime.now().strftime("ngày %d tháng %m năm 2022")
        self.BorrowFr.date.setText(date)
        note = " ..."
        self.BorrowFr.you_borrowed.setVisible(False)
        self.BorrowFr.refirm.setText(note)
        # setup account
        self.mail = JsonFirestore.loadRFIDmail(self.RFID)
        self.name = JsonFirestore.loadRFIDname(self.RFID)
        self.BorrowFr.name.setText(self.name)
        self.BorrowFr.email.setText("Email: " + self.mail.replace("_", ".").replace(".sis", "@sis"))

        re_minute = int(datetime.datetime.now().strftime("%M"))
        re_hour = int(datetime.datetime.now().strftime("%H"))
        i_minute = re_minute + 30 if re_minute < 30 else re_minute - 30
        i_hour = re_hour if re_minute < 30 else re_hour + 1
        str_minute = str(i_minute) if i_minute > 9 else f"0{i_minute}"
        str_hour = str(i_hour) if i_hour > 9 else f"0{i_hour}"
        timenow = str_hour + "-" + str_minute
        today = str(translateDate[str(datetime.datetime.now().strftime("%A"))])
        print("to day is :" + today)
        sections = JsonFirestore.readScheToday(self.RFID)
        self.check = 0
        self.time_ = datetime.datetime.now().strftime("%Y-%m-%d-%H-%M")
        for section in sections:
            section = JsonFirestore.readScheToday(self.RFID)[section]
            start = str(section["period"]).split("-")[0]
            end = str(section["period"]).split("-")[1]
            if (period[int(start)][0]) < timenow:
                if (period[int(end)][1]) > timenow:
                    self.check = 1
                    room = section["room"]
                    subject = section["subject"]

                    self.BorrowFr.room.setText("Phòng " + room + " - D6")
                    self.BorrowFr.subject.setText(subject)
                    self.BorrowFr.code.setText(str(random.randrange(100000, 999999)))
                    self.BorrowFr.label_.setText("|")
                    self.BorrowFr.start.setText(period[int(start)][0])
                    self.BorrowFr.end.setText(period[int(end)][1])
                    self.data = {
                        "borrow_tm": self.time_,
                        "return_tm": "_",
                        "period_tm": section["period"],
                        "teachroom": room,
                        "_subject_": subject
                    }

                    self.BorrowDialog.close()
                    self.numberB = 0
                    self.success.setVisible(True)
                    print("ok")
                    QtCore.QTimer.singleShot(1000, self.closeSuccess)
                    self.resetRemainTime()

                    print(room + " " + subject)
        if self.check == 0:
            self.BorrowFr.start.setText("")
            self.BorrowFr.end.setText("")
            self.BorrowFr.label_.setText("")
            self.BorrowFr.subject.setText("")
            self.BorrowFr.code.setText("")
            self.BorrowFr.room.setText("                  ...")
            self.data = {"borrow_tm": self.time_,
                         "return_tm": "_",
                         "period_tm": "_",
                         "teachroom": "_",
                         "_subject_": "_"}

        self.resetRemainTime()
        blur = QGraphicsBlurEffect()
        blur.setBlurRadius(0)
        self.BorrowDialog.setGraphicsEffect(blur)
        GIF_ = QMovie("Images/countdown.gif")
        self.BorrowFr.remainTime.setMovie(GIF_)
        GIF_.start()
        # self.BorrowDialog.showFullScreen()
        self.BorrowDialog.show()
        self.timeShow = datetime.datetime.now().strftime("%M-%S")
        QtCore.QTimer.singleShot(29500, self.closeDialog)
        self.numberB = 1
        self.BorrowFr.back.clicked.connect(self.BorrowDialog_close)
        self.BorrowFr.confirm.clicked.connect(self.Borrow)
        self.BorrowFr.continue_.clicked.connect(self.resetRemainTime)
        self.BorrowFr.broken.clicked.connect(self.showBroken)

    def Borrow(self):
        if self.BorrowFr.del_remote.isVisible() \
                or self.BorrowFr.del_hdmi.isVisible() \
                or self.BorrowFr.del_laser.isVisible() \
                or self.BorrowFr.del_micro.isVisible():
            self.data.update(
                {"ac_remote": self.BorrowFr.remote_room.text()[:3] if self.BorrowFr.del_remote.isVisible() else "_",
                 "laser_pen": self.BorrowFr.laser_room.text()[:3] if self.BorrowFr.del_laser.isVisible() else "_",
                 "mcr_phone": self.BorrowFr.micro_room.text()[:3] if self.BorrowFr.del_micro.isVisible() else "_",
                 "hdmi_wire": self.BorrowFr.hdmi_room.text()[:3] if self.BorrowFr.del_hdmi.isVisible() else "_", })
            self.bits = ""
            bit0 = "1" if self.BorrowFr.del_remote.isVisible() else "0"
            bit1 = "1" if self.BorrowFr.del_hdmi.isVisible() else "0"
            bit2 = "1" if self.BorrowFr.del_laser.isVisible() else "0"
            bit3 = "1" if self.BorrowFr.del_micro.isVisible() else "0"
            self.bits = bit0 + bit1 + bit2 + bit3
            self.last = {
                "Bits_AHLM": self.bits,
                "LastCheck": self.time_,
                "LastState": "Borrowed"
            }
            if self.check == 1:
                db.collection("History").document(self.mail).collection("EquipmentState") \
                    .document(self.time_).set(self.data)
                db.collection("History").document(self.mail).collection("EquipmentState") \
                    .document("Last").set(self.last)
                self.BorrowDialog.close()
                self.numberB = 0
                self.success.setVisible(True)
                print("ok")
                QtCore.QTimer.singleShot(1000, self.closeSuccess)
            if self.check == 0:
                re_minute = int(datetime.datetime.now().strftime("%M"))
                re_hour = int(datetime.datetime.now().strftime("%H"))
                i_minute = re_minute + 30 if re_minute < 30 else re_minute - 30
                i_hour = re_hour if re_minute < 30 else re_hour + 1
                str_minute = str(i_minute) if i_minute > 9 else f"0{i_minute}"
                str_hour = str(i_hour) if i_hour > 9 else f"0{i_hour}"
                timenow = str_hour + "-" + str_minute
                today = str(translateDate[str(datetime.datetime.now().strftime("%A"))])
                ids = JsonFirestore.loadRFID()
                try:
                    for id in ids:
                        id = JsonFirestore.loadRFID()[id]
                        sections = id[today]
                        for section in sections:
                            section = sections[section]
                            start = str(section["period"]).split("-")[0]
                            end = str(section["period"]).split("-")[1]
                            if (period[int(start)][0]) < timenow:
                                if (period[int(end)][1]) > timenow:
                                    self.data.update({"period_tm": section["period"],
                                                      "teachroom": section["room"],
                                                      "_subject_": section["subject"]})
                except:
                    pass
                db.collection("History").document(self.mail).collection("EquipmentState") \
                    .document(self.time_).set(self.data)
                db.collection("History").document(self.mail).collection("EquipmentState") \
                    .document("Last").set(self.last)
                self.BorrowDialog.close()
                self.numberB = 0
                self.success.setVisible(True)
                print("ok")
                QtCore.QTimer.singleShot(1000, self.closeSuccess)
        else:
            self.BorrowFr.label.setVisible(True)
            QtCore.QTimer.singleShot(1000, self.hideWarn)

    def hideWarn(self):
        self.BorrowFr.label.setVisible(False)

    def Return(self):
        self.ReturnFr.confirm.setEnabled(False)
        time = datetime.datetime.now().strftime("%Y-%m-%d-%H-%M")
        db.collection("History").document(self.mail).collection("EquipmentState") \
            .document(self.Last["LastCheck"]).update({"return_tm": time})
        db.collection("History").document(self.mail).collection("EquipmentState") \
            .document("Last").update({"LastState": "Returned", "LastCheck": "_", "Bits_AHLM": "_"})

        self.ReturnDialog.close()
        self.numberR = 0
        self.success.setVisible(True)
        print("ok")
        QtCore.QTimer.singleShot(1000, self.closeSuccess)

    def showBroken(self):
        self.BrokenFr = BrokenF()
        self.BrokenDialog = QDialog()
        self.BrokenFr.setupUi(self.BrokenDialog)
        self.BrokenDialog.setWindowFlag(Qt.FramelessWindowHint, True)
        self.BrokenDialog.setAttribute(QtCore.Qt.WidgetAttribute.WA_TranslucentBackground)

        self.BrokenFr.back.clicked.connect(self.closeBroken)
        self.BrokenFr.pushButton.clicked.connect(self.Report)

        blur = QGraphicsBlurEffect()
        blur.setBlurRadius(20)
        if self.numberB == 1:
            self.BorrowDialog.setGraphicsEffect(blur)
        if self.numberR == 1:
            self.ReturnDialog.setGraphicsEffect(blur)
        self.BrokenDialog.show()
        self.numberBroken = 1

    def Report(self):
        time_ = datetime.datetime.now().strftime("%Y-%m-%d-%H-%M")
        if self.BrokenFr.del_remote.isVisible():
            db.collection("Broken").document("broken").collection("ac_remote")\
                .document(self.BrokenFr.remote_room.text()).set({
                "detected": time_,
                "fixed": "_",
                "state": "not fixed"
            })
        if self.BrokenFr.del_hdmi.isVisible():
            db.collection("Broken").document("broken").collection("hdmi_wire")\
                .document(self.BrokenFr.hdmi_room.text()).set({
                "detected": time_,
                "fixed": "_",
                "state": "not fixed"
            })
        if self.BrokenFr.del_laser.isVisible():
            db.collection("Broken").document("broken").collection("laser_pen")\
                .document(self.BrokenFr.laser_room.text()).set({
                "detected": time_,
                "fixed": "_",
                "state": "not fixed"
            })
        if self.BrokenFr.del_laser.isVisible():
            db.collection("Broken").document("broken").collection("mcr_phone")\
                .document(self.BrokenFr.micro_room.text()).set({
                "detected": time_,
                "fixed": "_",
                "state": "not fixed"
            })
        self.BrokenFr.done.setVisible(True)
        QtCore.QTimer.singleShot(1000, self.closeBroken)

    def closeBroken(self):
        blur = QGraphicsBlurEffect()
        blur.setBlurRadius(0)
        if self.numberB == 1:
            self.BorrowDialog.setGraphicsEffect(blur)
        if self.numberR == 1:
            self.ReturnDialog.setGraphicsEffect(blur)
        self.BrokenDialog.close()
        self.numberBroken = 0
        self.resetRemainTime()


    def resetRemainTime(self):
        QtCore.QTimer.singleShot(29000, self.closeDialog)
        if self.numberB == 1:
            GIF_ = QMovie("Images/countdown.gif")
            self.BorrowFr.remainTime.setMovie(GIF_)
            GIF_.start()
            self.timeShow = datetime.datetime.now().strftime("%M-%S")
        if self.numberR == 1:
            GIF_ = QMovie("Images/countdown.gif")
            self.ReturnFr.remainTime.setMovie(GIF_)
            GIF_.start()
            self.timeShow = datetime.datetime.now().strftime("%M-%S")

    def closeDialog(self):
        lastsecond = int(self.timeShow.split("-")[1])
        lastminute = int(self.timeShow.split("-")[0])
        thissecond = int(datetime.datetime.now().strftime("%S"))
        thisminute = int(datetime.datetime.now().strftime("%M"))

        print(lastsecond)
        print(thissecond)
        print(thisminute)
        print(lastminute)

        if lastsecond > 29 and thissecond > lastsecond - 32 and thisminute == lastminute + 1:
            if self.numberB == 1 and self.numberBroken == 0:
                self.BorrowDialog.close()
            if self.numberR == 1:
                self.ReturnDialog.close()

        if lastsecond < 30 and thissecond > lastsecond + 28 and thisminute == lastminute:
            if self.numberB == 1 and self.numberBroken == 0:
                self.BorrowDialog.close()
            if self.numberR == 1:
                self.ReturnDialog.close()

    def closeSuccess(self):
        self.success.setVisible(False)

    def BorrowDialog_close(self):
        self.BorrowDialog.close()
        self.numberB = 0

    def ReturnDialog_close(self):
        self.ReturnDialog.close()
        self.numberR = 0

    def reformTime(self, time):
        rfTime = ""
        time = time.split("-")
        rfTime = f'\n.. ngày {time[2]}/{time[1]}/{time[0]} lúc {time[3]}:{time[4]}'
        return rfTime


def main():
    window = QMainWindow()
    ui = MainF()
    ui.setupUi(window)
    # window.showFullScreen()
    window.show()
    ui.pushButton.clicked.connect(window.close)
    sys.exit(app.exec_())


if __name__ == "__main__":
    main()
