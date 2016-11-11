<%-- 
    Document   : SummarySocialGraph
    Created on : 08 Oct 2016, 6:13:03 PM
    Author     : kryli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/styles/font-awesome.min.css" />"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/styles/alchemy.css" />"/>

    <script src="<c:url value="/resources/scripts/d3.js" />"></script>
    <script src="<c:url value="/resources/scripts/lodash.min.js" />"></script>
    <script src="<c:url value="/resources/scripts/alchemy.js" />"></script>

    <script type="text/javascript">
        var hiddenGraph = false;
        function MinimizeClick_Graph() {
            hiddenGraph = !hiddenGraph;
            if (hiddenGraph) {
                $('#socialGraphDiv').hide(1000);
                document.getElementById('btnMinimize_Graph').innerHTML = '<span class="glyphicon glyphicon-menu-down"></span>';
            } else {
                $('#socialGraphDiv').show(1000);
                document.getElementById('btnMinimize_Graph').innerHTML = '<span class="glyphicon glyphicon-menu-up"></span>';
            }
        }

        function loadSocialGraph() {
            var width = $("#socialGraphDiv").parent().width() - 40;
            var height = $("#socialGraphDiv").parent().height();

            var config = {
                dataSource: "<c:url value="/resources/social_graph/${currentCompany}.json" />",
                divSelector: "#alchemy",
                forceLocked: true,
                zoomControls: true,
                directedEdges: true,
                curvedEdges:true,
                backgroundColour: "#FFFFFF",
                graphHeight: function () {
                    if (height > 1024) {
                        height = 1024;
                    }
                    return height;
                },
                graphWidth: function () {
                    return width;
                },
                linkDistance: function () {
                    return 10;
                },
                nodeTypes: {
                    "node_type": ["POST",
                        "FB_USER"]
                },
                nodeStyle: {
                    "all": {
                        "borderColor": "#127DC1",
                        "borderWidth": function(d, radius) {
                            return radius / 4
                        },
                        "color": function(d) {
                            if(d.getProperties().root)
                                return "rgba(20, 20, 20, 1 )";
                            else return "rgba(104, 185, 254, 1 )"
                        },
                        "radius": function(d) {
                            if(d.getProperties().root)
                                return 15; else return 10
                        },
                    },
                    "FB_USER":{
                        "color": "#000067",
                        "selected": {
                            "color": "#ffffff",
                        },
                        "highlighted": {
                            "color": "#b4dcff"
                        }
                    }
                },
                edgeStyle: {
                    "all": {
                        "width": function(d) {
                            return (d.getProperties().load + 0.5) * 1.3
                        },
                        "color": function(d) {
                            return "rgba(0, 0, 0, 0.5)"
                        }
                    }
                },
                nodeCaptionsOnByDefault: true,
                initialScale: 0.75,
                initialTranslate: [(width / 6), (height / 6)],
                nodeCaption: function (node) {
                    return node.caption;
                }
            };
            new Alchemy(config);
            $('#loadingSocialGraph').fadeOut(1000);
        }
    </script>
</head>

<body>

<div id="loadingSocialGraph" class="alert alert-info">
    <strong>Loading!</strong> We are busy processing the social graph. This won't take long.
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <button id="btnMinimize_Graph" class="btn btn-primary btn-sm" onclick="MinimizeClick_Graph()">
                <span class="glyphicon glyphicon-menu-up"></span>
            </button>
            <button class="btn btn-primary btn-sm" onclick="loadSocialGraph()" data-toggle="tooltip"
                    data-placement="top" title="" data-original-title="Graph not loading? Click here to refresh.">
                <span class="glyphicon glyphicon-repeat"></span>
            </button>

            <label>${currentCompany}</label>
            Social Graph
        </h3>
    </div>
    <div class="panel-body" id="socialGraphDiv"
         style="width: 100%; min-height: 600px; height: 100%; max-height: 1440px">
        <div id="alchemy" class="alchemy">
        </div>
    </div>
</div>
</body>
</html>
