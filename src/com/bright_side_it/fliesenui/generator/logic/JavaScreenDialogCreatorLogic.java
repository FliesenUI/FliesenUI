package com.bright_side_it.fliesenui.generator.logic;

import com.bright_side_it.fliesenui.generator.util.GeneratorConstants;
import com.bright_side_it.fliesenui.generator.util.GeneratorUtil;
import com.bright_side_it.fliesenui.screendefinition.model.ScreenDefinition;

public class JavaScreenDialogCreatorLogic {
    public StringBuilder createShowMessageFunction(ScreenDefinition screenDefinition) {
        StringBuilder result = new StringBuilder();
        result.append("    $scope.showMessage = function (message) {\n");
        result.append("     if (message.typeID == 201){\n");
        result.append("             $mdToast.show($mdToast.simple().textContent(message.text).hideDelay(3000));\n");
        result.append("     } else if (message.typeID == 101) {\n");
        result.append("         $mdDialog.show(\n");
        result.append("                   $mdDialog.alert()\n");
        result.append("                     .parent(angular.element(document.querySelector('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')))\n");
        result.append("                     .clickOutsideToClose(true)\n");
        result.append("                     .title(message.title)\n");
        result.append("                     .textContent(message.text)\n");
        result.append("                     .ariaLabel('Info Dialog' + message.title + \", \" + message.text)\n");
        result.append("                     .ok('OK')\n");
        result.append("                 );\n");
        result.append("     } else if (message.typeID == 102) {\n");
        result.append("         $mdDialog.show(\n");
        result.append("                 $mdDialog.alert()\n");
        result.append("                 .parent(angular.element(document.querySelector('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')))\n");
        result.append("                 .clickOutsideToClose(true)\n");
        result.append("                 .title(\"WARNING: \" + message.title)\n");
        result.append("                 .textContent(message.text)\n");
        result.append("                 .ariaLabel('Warning Dialog' + message.title + \", \" + message.text)\n");
        result.append("                 .ok('OK')\n");
        result.append("         );\n");
        result.append("     } else if (message.typeID == 103) {\n");
        result.append("         $mdDialog.show(\n");
        result.append("                 $mdDialog.alert()\n");
        result.append("                 .parent(angular.element(document.querySelector('" + GeneratorUtil.getHTMLScreenPanelName(screenDefinition) + "')))\n");
        result.append("                 .clickOutsideToClose(true)\n");
        result.append("                 .title(\"ERROR: \" + message.title)\n");
        result.append("                 .textContent(message.text)\n");
        result.append("                 .ariaLabel('Warning Dialog' + message.title + \", \" + message.text)\n");
        result.append("                 .ok('OK')\n");
        result.append("         );\n");
        result.append("     }\n");
        result.append("    }\n");
        return result;
    }

    public StringBuilder createInputDialogFunction(String screenIDPrefix) {
        StringBuilder result = new StringBuilder();
        result.append("    $scope.showInputDialog = function(referenceID, title, textContent, label, initialValueText, okText, cancelText) {\n");
        result.append("        var confirm = $mdDialog.prompt()\n");
        result.append("          .title(title)\n");
        result.append("          .textContent(textContent)\n");
        result.append("          .placeholder(label)\n");
        result.append("          .ariaLabel(label)\n");
        result.append("          .initialValue(initialValueText)\n");
        result.append("          .ok(okText)\n");
        result.append("          .cancel(cancelText);\n");
        result.append("\n");
        result.append("        $mdDialog.show(confirm).then(function(result) {\n");
        result.append("            var request = " + screenIDPrefix + "createRequest(\"" + GeneratorConstants.REQUEST_ACTION_ON_INNPUT_DIALOG_RESULT + "\");\n");
        result.append("            request.parameters[\"referenceID\"] = referenceID;\n");
        result.append("            if (typeof result != \"undefined\"){\n");
        result.append("                request.parameters[\"result\"] = result;\n");
        result.append("                " + screenIDPrefix + "executeRequest(request);\n");
        result.append("            } else {\n");
        result.append("                request.parameters[\"result\"] = \"\";\n");
        result.append("                " + screenIDPrefix + "executeRequest(request);\n");
        result.append("            }\n");
        result.append("        }, function() {\n");
        result.append("            var request = " + screenIDPrefix + "createRequest(\"onInputDialogResult\");\n");
        result.append("            request.parameters[\"referenceID\"] = referenceID;\n");
        result.append("            " + screenIDPrefix + "executeRequest(request);\n");
        result.append("        });\n");
        result.append("    };\n");
        result.append("\n");
        return result;
    }

    public StringBuilder createConfirmDialogFunction(String screenIDPrefix) {
        StringBuilder result = new StringBuilder();

        result.append("    $scope.showConfirm = function(referenceID, title, textContent, okText, cancelText) {\n");
        result.append("        var confirm = $mdDialog.confirm()\n");
        result.append("              .title(title)\n");
        result.append("              .textContent(textContent)\n");
        result.append("              .ariaLabel(title)\n");
        result.append("              .ok(okText)\n");
        result.append("              .cancel(cancelText);\n");
        result.append("\n");
        result.append("        $mdDialog.show(confirm).then(function() {\n");
        result.append("            var request = " + screenIDPrefix + "createRequest(\"" + GeneratorConstants.REQUEST_ACTION_ON_CONFIRM_DIALOG_RESULT + "\");\n");
        result.append("            request.parameters[\"referenceID\"] = referenceID;\n");
        result.append("            request.parameters[\"result\"] = true;\n");
        result.append("            " + screenIDPrefix + "executeRequest(request);\n");
        result.append("        }, function() {\n");
        result.append("            var request = " + screenIDPrefix + "createRequest(\"onConfirmDialogResult\");\n");
        result.append("            request.parameters[\"referenceID\"] = referenceID;\n");
        result.append("            request.parameters[\"result\"] = false;\n");
        result.append("            " + screenIDPrefix + "executeRequest(request);\n");
        result.append("        });\n");
        result.append("    };\n");
        result.append("\n");

        return result;
    }

}
