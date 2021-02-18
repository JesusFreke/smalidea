/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jf.smalidea.errorReporting;

import com.intellij.diagnostic.IdeErrorsDialog;
import com.intellij.diagnostic.ReportMessages;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.idea.IdeaLogger;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.updateSettings.impl.UpdateSettings;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import com.intellij.util.SystemProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sends crash reports to Github.
 *
 * Based on the go-lang plugin's error reporter
 * (https://github.com/dlsniper/google-go-lang-idea-plugin/commit/c451006cc9fc926ca347189951baa94f4032c5c4)
 */
public class ErrorReporter extends ErrorReportSubmitter {

  @Override
  public String getReportActionText() {
    return "Report as issue on smali's github repo";
  }


  @Override
  public boolean submit(IdeaLoggingEvent[] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
    IdeaLoggingEvent event = events[0];

    final DataContext dataContext = DataManager.getInstance().getDataContext(parentComponent);
    IdeaPluginDescriptor plugin = IdeErrorsDialog.getPlugin(event);
    if (plugin == null) {
      plugin = PluginManager.getPlugin(PluginId.getId("org.jf.smalidea"));
    }

    Map<String, String> reportValues = createParameters(plugin, additionalInfo, event.getMessage());

    final Project project = CommonDataKeys.PROJECT.getData(dataContext);

    Consumer<String> successCallback = new Consumer<String>() {
      @Override
      public void consume(String token) {
        final SubmittedReportInfo reportInfo = new SubmittedReportInfo(
                null, "Issue " + token, SubmittedReportInfo.SubmissionStatus.NEW_ISSUE);
        consumer.consume(reportInfo);

        ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
                "Submitted",
                NotificationType.INFORMATION,
                null).setImportant(false).notify(project);
      }
    };

    Consumer<Exception> errorCallback = new Consumer<Exception>() {
      @Override
      public void consume(Exception e) {
        String message = String.format("<html>There was an error while creating a GitHub issue: %s<br>" +
                "Please consider manually creating an issue on the " +
                "<a href=\"https://github.com/JesusFreke/smali/issues\">Smali Issue Tracker</a></html>",
                e.getMessage());
        ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
                message,
                NotificationType.ERROR,
                NotificationListener.URL_OPENING_LISTENER).setImportant(false).notify(project);
      }
    };

    GithubFeedbackTask task = new GithubFeedbackTask(
            project,
            "Submitting error report",
            true,
            reportValues,
            successCallback, errorCallback);

    if (project == null) {
      task.run(new EmptyProgressIndicator());
    } else {
      ProgressManager.getInstance().run(task);
    }
    return true;
  }

  public static Map<String, String> createParameters(
          @Nullable IdeaPluginDescriptor plugin,
          String errorDescription,
          String errorMessage) {

    Map<String, String> params = new LinkedHashMap<>();

    params.put("protocol.version", "1");

    params.put("os.name", SystemProperties.getOsName());
    params.put("java.version", SystemProperties.getJavaVersion());
    params.put("java.vm.vendor", SystemProperties.getJavaVmVendor());

    ApplicationInfoEx appInfo = ApplicationInfoEx.getInstanceEx();
    ApplicationNamesInfo namesInfo = ApplicationNamesInfo.getInstance();
    Application application = ApplicationManager.getApplication();
    params.put("app.name", namesInfo.getProductName());
    params.put("app.name.full", namesInfo.getFullProductName());
    params.put("app.name.version", appInfo.getVersionName());
    params.put("app.eap", Boolean.toString(appInfo.isEAP()));
    params.put("app.internal", Boolean.toString(application.isInternal()));
    params.put("app.build", appInfo.getBuild().asString());
    params.put("app.version.major", appInfo.getMajorVersion());
    params.put("app.version.minor", appInfo.getMinorVersion());
    params.put("app.build.date", formatDate(appInfo.getBuildDate()));
    params.put("app.build.date.release", formatDate(appInfo.getMajorReleaseBuildDate()));

    UpdateSettings updateSettings = UpdateSettings.getInstance();
    params.put("update.channel.status", updateSettings.getSelectedChannelStatus().getCode());
    params.put("update.ignored.builds", StringUtil.join(updateSettings.getIgnoredBuildNumbers(), ","));

    if (plugin != null) {
      params.put("plugin.name", plugin.getName());
      params.put("plugin.version", plugin.getVersion());
    }

    params.put("last.action", IdeaLogger.ourLastActionId);

    params.put("error.message", errorMessage);
    params.put("error.description", errorDescription);

    return params;
  }

  private static String formatDate(Calendar calendar) {
    return calendar == null ?  null : Long.toString(calendar.getTime().getTime());
  }
}
