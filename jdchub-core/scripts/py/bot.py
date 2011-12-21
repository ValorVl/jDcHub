# -*- coding: utf-8 -*-
__author__ = 'lh'

import sys

from ru.sincore.client import AbstractClient
from ru.sincore.TigerImpl import CIDGenerator
from ru.sincore.adc import ClientType
from ru.sincore.util import AdcUtils
from ru.sincore.adc.action.actions import AbstractAction
from ru.sincore import ClientManager

class PythonBot(AbstractClient):
    def __init__(self):
        self.botName = "PythonBot"

    def getINF(self):
        return "BINF " + self.getSid() + " ID" + self.getCid() + " NI" + self.getNick() + " DE" + self.getDescription() + " CT" + str(self.getClientType())

    def sendAdcAction(self, action):
        return

if __name__ == "__main__":
    bot = PythonBot()
    bot.setSid("PBOT")
    bot.setCid(CIDGenerator.generate())
    bot.setNick("PythonBot")
    bot.setDescription(AdcUtils.toAdcString("Python bot"))
    bot.setWeight(10)
    bot.setClientType(ClientType.BOT)
    bot.setValidated();

    ClientManager.getInstance().addClient(bot)