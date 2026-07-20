const chartOption = {};
chartOption.getFun = function (optionStr) {
  let funStr = 'function (echarts,echartData) {\n' + optionStr + '\n' + '    return option;\n' + '}';
  return funStr;
};
chartOption.getOptionMap = function () {
  let optionStr =
    'let option={\n' +
    '    "tooltip": {\n' +
    '        "trigger": "item"\n' +
    '    },\n' +
    '    "dataRange": {\n' +
    '        "min": 30,\n' +
    '        "max": 700,\n' +
    '        "splitNumber": 0,\n' +
    '        "text": [\n' +
    '            "高",\n' +
    '            "低"\n' +
    '        ],\n' +
    '        "realtime": false,\n' +
    '        "calculable": false,\n' +
    '        "selectedMode": false,\n' +
    '        "itemWidth": 10,\n' +
    '        "itemHeight": 60,\n' +
    '        "inRange": {\n' +
    '            "color": [\n' +
    '                "lightskyblue",\n' +
    '                "yellow",\n' +
    '                "orangered"\n' +
    '            ]\n' +
    '        }\n' +
    '    },\n' +
    '    "series": [\n' +
    '        {\n' +
    '            "type": "map",\n' +
    '            "map": "mapJson",\n' +
    '            "scaleLimit": {\n' +
    '                "min": 0.8,\n' +
    '                "max": 1.9\n' +
    '            },\n' +
    '            "mapLocation": {\n' +
    '                "y": 60\n' +
    '            },\n' +
    '            "itemSytle": {\n' +
    '                "emphasis": {\n' +
    '                    "label": {\n' +
    '                        "show": false\n' +
    '                    }\n' +
    '                }\n' +
    '            },\n' +
    '            "label": {\n' +
    '                "normal": {\n' +
    '                    "show": true\n' +
    '                },\n' +
    '                "emphasis": {\n' +
    '                    "show": true\n' +
    '                }\n' +
    '            },\n' +
    '            "data": [\n' +
    '                {\n' +
    '                    "name": "郑州市",\n' +
    '                    "value": "585"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "洛阳市",\n' +
    '                    "value": "450"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "许昌市",\n' +
    '                    "value": "256"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "开封市",\n' +
    '                    "value": "398"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "平顶山市",\n' +
    '                    "value": "444"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "安阳市",\n' +
    '                    "value": "74"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "鹤壁市",\n' +
    '                    "value": "127"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "新乡市",\n' +
    '                    "value": "269"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "焦作市",\n' +
    '                    "value": "36"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "濮阳市",\n' +
    '                    "value": "187"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "漯河市",\n' +
    '                    "value": "33"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "三门峡市",\n' +
    '                    "value": "98"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "商丘市",\n' +
    '                    "value": "254"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "周口市",\n' +
    '                    "value": "87"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "驻马店市",\n' +
    '                    "value": "76"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "南阳市",\n' +
    '                    "value": "325"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "信阳市",\n' +
    '                    "value": "333"\n' +
    '                },\n' +
    '                {\n' +
    '                    "name": "济源市",\n' +
    '                    "value": "15"\n' +
    '                }\n' +
    '            ]\n' +
    '        }\n' +
    '    ]\n' +
    '}';
  return optionStr;
};
chartOption.getOption = function () {
  let optionStr =
    "let xaxisData = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']\n" +
    'let yaxisData = [1600, 1880, 1100, 2200, 2400, 1350, 1180, 2500, 1800, 1400, 1950, 1580]\n' +
    'const offsetX = 10;\n' +
    'const offsetY = 5;\n' +
    '// 绘制左侧面\n' +
    'const CubeLeft = echarts.graphic.extendShape({\n' +
    '   shape: {\n' +
    '      x: 0,\n' +
    '      y: 0,\n' +
    '   },\n' +
    '   buildPath: function (ctx, shape) {\n' +
    '      // 会canvas的应该都能看得懂，shape是从custom传入的\n' +
    '      const xAxisPoint = shape.xAxisPoint;\n' +
    '      const c0 = [shape.x, shape.y];\n' +
    '      const c1 = [shape.x - offsetX, shape.y - offsetY];\n' +
    '      const c2 = [xAxisPoint[0] - offsetX, xAxisPoint[1] - offsetY];\n' +
    '      const c3 = [xAxisPoint[0], xAxisPoint[1]];\n' +
    '      ctx\n' +
    '         .moveTo(c0[0], c0[1])\n' +
    '         .lineTo(c1[0], c1[1])\n' +
    '         .lineTo(c2[0], c2[1])\n' +
    '         .lineTo(c3[0], c3[1])\n' +
    '         .closePath();\n' +
    '   },\n' +
    '});\n' +
    '\n' +
    '// 绘制右侧面\n' +
    'const CubeRight = echarts.graphic.extendShape({\n' +
    '   shape: {\n' +
    '      x: 0,\n' +
    '      y: 0,\n' +
    '   },\n' +
    '   buildPath: function (ctx, shape) {\n' +
    '      const xAxisPoint = shape.xAxisPoint;\n' +
    '      const c1 = [shape.x, shape.y];\n' +
    '      const c2 = [xAxisPoint[0], xAxisPoint[1]];\n' +
    '      const c3 = [xAxisPoint[0] + offsetX, xAxisPoint[1] - offsetY];\n' +
    '      const c4 = [shape.x + offsetX, shape.y - offsetY];\n' +
    '      ctx\n' +
    '         .moveTo(c1[0], c1[1])\n' +
    '         .lineTo(c2[0], c2[1])\n' +
    '         .lineTo(c3[0], c3[1])\n' +
    '         .lineTo(c4[0], c4[1])\n' +
    '         .closePath();\n' +
    '   },\n' +
    '});\n' +
    '\n' +
    '// 绘制顶面\n' +
    'const CubeTop = echarts.graphic.extendShape({\n' +
    '   shape: {\n' +
    '      x: 0,\n' +
    '      y: 0,\n' +
    '   },\n' +
    '   buildPath: function (ctx, shape) {\n' +
    '      const c1 = [shape.x, shape.y];\n' +
    '      const c2 = [shape.x + offsetX, shape.y - offsetY]; //右点\n' +
    '      const c3 = [shape.x, shape.y - offsetX];\n' +
    '      const c4 = [shape.x - offsetX, shape.y - offsetY];\n' +
    '      ctx\n' +
    '         .moveTo(c1[0], c1[1])\n' +
    '         .lineTo(c2[0], c2[1])\n' +
    '         .lineTo(c3[0], c3[1])\n' +
    '         .lineTo(c4[0], c4[1])\n' +
    '         .closePath();\n' +
    '   },\n' +
    '});\n' +
    '\n' +
    '// 注册三个面图形\n' +
    'echarts.graphic.registerShape("CubeLeft", CubeLeft);\n' +
    'echarts.graphic.registerShape("CubeRight", CubeRight);\n' +
    'echarts.graphic.registerShape("CubeTop", CubeTop);\n' +
    '\n' +
    'option = {\n' +
    "   backgroundColor: 'black',\n" +
    '   tooltip: {\n' +
    '      trigger: "axis",\n' +
    '      axisPointer: {\n' +
    '         type: "shadow",\n' +
    '      },\n' +
    '      backgroundColor: "rgba(255,255,255,0.75)",\n' +
    '      extraCssText: "box-shadow: 2px 2px 4px 0px rgba(0,0,0,0.3);",\n' +
    '      textStyle: {\n' +
    '         fontSize: 14,\n' +
    '         color: "#000",\n' +
    '      },\n' +
    '      formatter: (params, ticket, callback) => {\n' +
    '         const item = params[1];\n' +
    '         return item.name + " : " + item.value + " 个";\n' +
    '      },\n' +
    '   },\n' +
    '   grid: {\n' +
    '      left: "1%",\n' +
    '      right: "0%",\n' +
    '      top: "16%",\n' +
    '      bottom: "5%",\n' +
    '      containLabel: true,\n' +
    '   },\n' +
    '   xAxis: {\n' +
    "      type: 'category',\n" +
    '      data: xaxisData,\n' +
    '      axisLine: {\n' +
    '         show: true,\n' +
    '         lineStyle: {\n' +
    '            width: 1,\n' +
    "            color: '#008de7'\n" +
    '         }\n' +
    '      },\n' +
    '      axisTick: {\n' +
    '         show: false\n' +
    '      },\n' +
    '      axisLabel: {\n' +
    '         fontSize: 14,\n' +
    "         color: '#FFFFFF'\n" +
    '      }\n' +
    '   },\n' +
    '   yAxis: {\n' +
    '      name:"万千瓦时",\n' +
    "      type: 'value',\n" +
    '      nameTextStyle: {\n' +
    "         color: '#fff',\n" +
    '         fontWeight: 400,\n' +
    '         fontSize: 14\n' +
    '      },\n' +
    '      axisLine: {\n' +
    '         show: true,\n' +
    '         lineStyle: {\n' +
    '            width: 1,\n' +
    "            color: '#008de7'\n" +
    '         }\n' +
    '      },\n' +
    '      splitLine: {\n' +
    '         show: true,\n' +
    '         lineStyle: {\n' +
    "            color: '#008de7',\n" +
    "            type: 'dashed'\n" +
    '         }\n' +
    '      },\n' +
    '      axisTick: {\n' +
    '         show: false\n' +
    '      },\n' +
    '      axisLabel: {\n' +
    '         fontSize: 14,\n' +
    "         color: '#FFFFFF'\n" +
    '      }\n' +
    '   },\n' +
    '   series: [\n' +
    '      {\n' +
    '         type: "custom",\n' +
    '         renderItem: (params, api) => {\n' +
    '            const location = api.coord([api.value(0), api.value(1)]);\n' +
    '            return {\n' +
    '               type: "group",\n' +
    '               children: [\n' +
    '                  {\n' +
    '                     type: "CubeLeft",\n' +
    '                     shape: {\n' +
    '                        api,\n' +
    '                        xValue: api.value(0),\n' +
    '                        yValue: api.value(1),\n' +
    '                        x: location[0],\n' +
    '                        y: location[1],\n' +
    '                        xAxisPoint: api.coord([api.value(0), 0]),\n' +
    '                     },\n' +
    '                     style: {\n' +
    '                        fill: new echarts.graphic.LinearGradient(0, 1, 0, 0, [\n' +
    '                           {\n' +
    '                              offset: 0,\n' +
    "                              color: '#09f7f9',\n" +
    '                           },\n' +
    '                           {\n' +
    '                              offset: 1,\n' +
    "                              color: '#09f7f920'\n" +
    '                           }\n' +
    '                        ]),\n' +
    '                     },\n' +
    '                  },\n' +
    '                  {\n' +
    '                     type: "CubeRight",\n' +
    '                     shape: {\n' +
    '                        api,\n' +
    '                        xValue: api.value(0),\n' +
    '                        yValue: api.value(1),\n' +
    '                        x: location[0],\n' +
    '                        y: location[1],\n' +
    '                        xAxisPoint: api.coord([api.value(0), 0]),\n' +
    '                     },\n' +
    '                     style: {\n' +
    '                        fill: new echarts.graphic.LinearGradient(0, 1, 0, 0, [\n' +
    '                           {\n' +
    '                              offset: 0,\n' +
    "                              color: '#00a6a7',\n" +
    '                           },\n' +
    '                           {\n' +
    '                              offset: 1,\n' +
    "                              color: '#00a6a720'\n" +
    '                           },\n' +
    '                        ]),\n' +
    '                     },\n' +
    '                  },\n' +
    '                  {\n' +
    '                     type: "CubeTop",\n' +
    '                     shape: {\n' +
    '                        api,\n' +
    '                        xValue: api.value(0),\n' +
    '                        yValue: api.value(1),\n' +
    '                        x: location[0],\n' +
    '                        y: location[1],\n' +
    '                        xAxisPoint: api.coord([api.value(0), 0]),\n' +
    '                     },\n' +
    '                     style: {\n' +
    "                        fill: '#09f7f8'\n" +
    '                     },\n' +
    '                  },\n' +
    '               ],\n' +
    '            };\n' +
    '         },\n' +
    '         data: yaxisData,\n' +
    '      },\n' +
    '      {\n' +
    '         type: "bar",\n' +
    '         itemStyle: {\n' +
    '            color: "transparent",\n' +
    '         },\n' +
    '         data: yaxisData,\n' +
    '      },\n' +
    '   ],\n' +
    '};\n';
  return optionStr;
};
export default chartOption;
