# -*- coding: utf-8 -*-
__author__ = 'lh'

import sys

from ru.sincore import ClientManager, Broadcast
from ru.sincore.adc import ClientType, MessageType
from ru.sincore.adc.action.actions import AbstractAction
from ru.sincore.client import AbstractClient
from ru.sincore.TigerImpl import CIDGenerator
from ru.sincore.util import AdcUtils
from ru.sincore.Exceptions import CommandException

from ru.sincore.adc.action.actions import MSG

from feedparser import feedparser
from threading import Timer


# Simple bot
# Now bot can get rss feed from site and show last new entry
class PythonBot(AbstractClient):
    """A simple python bot"""
    def __init__(self):
        self.nick = "ChatBot"
        self.rss = "http://korobka.tv/rss.xml"
        self.lastFeedTitle = "first_last_feed_title"


    # this method give a reaction to incoming actions
    # like messages (MSG class), searchs (SCH class) etc
    def sendAdcAction(self, action):
        return


    def sendMessageToChat(self, message):
        outgoingMessage = MSG()
        outgoingMessage.setMessage(message)
        outgoingMessage.setMessageType(MessageType.B)
        outgoingMessage.setSourceSID(self.getSid())
        # send outgoing message
        Broadcast.getInstance().broadcast(outgoingMessage.getRawCommand(), self);
        return


    def sendRawCommand(self, rawCommand):
        # create MSG object from rawCommand
        message = MSG(rawCommand)

        # trying to get message (to be sure rawCommand is a MSG action)
        try:
            messageString = message.getMessage()
        except CommandException:
            return

        # trying to find substring with word 'bor'
        if messageString.find("bor") == 0:
            # construct outgoing message to send it to chat
            self.sendMessageToChat(u"Кто-то жаждит бор!")
        return


    # get's rss feed from site and send updates to chat
    def getRss(self):
        # get rss feed
        feed = feedparser.parse(self.rss)

        # if new feed not equal to last feed
        if self.lastFeedTitle != feed.entries[0].title:
            self.lastFeedTitle = feed.entries[0].title
            # compose message
            outgoingMessage = feed.entries[0].title + "\n" + feed.entries[0].link
            self.sendMessageToChat(outgoingMessage);

        t = Timer(10.0, self.getRss)
        t.start()
        return


    def removeSession(self, immediately):
        # construct IQUI message
        ClientManager.getInstance().removeClient(self);
        # broadcast bot quited message
        Broadcast.getInstance().broadcast("IQUI " + self.getSid(), self);



# main function
if __name__ == "__main__":
    bot = PythonBot()
    bot.setSid("PBOT")
    bot.setCid(CIDGenerator.generate())
    bot.setDescription(AdcUtils.toAdcString(u"Я могу много чего, спроси меня"))
    bot.setEmail(u"lh@podryad.tv")
    bot.setWeight(10)
    bot.setClientType(ClientType.BOT)
    bot.setValidated()
    bot.setActive(True)
    bot.setMustBeDisconnected(False)

    ClientManager.getInstance().addClient(bot)
    Broadcast.getInstance().broadcast(bot.getINF(), bot);

    t = Timer(10.0, bot.getRss)
    t.start()
