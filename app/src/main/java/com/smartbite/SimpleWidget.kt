package com.smartbite
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import SimpleWidgetContent

class SimpleWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SimpleWidgetContent()
}
