/*
 * Copyright (c) 2015, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

let noResult = {l: "No results found"};
let loading = {l: "Loading search index..."};
let catModules = "Modules";
let catPackages = "Packages";
let catTypes = "Classes and Interfaces";
let catMembers = "Members";
let catSearchTags = "Search Tags";
let highlight = "<span class=\"result-highlight\">$&</span>";
let searchPattern = "";
let fallbackPattern = "";
let RANKING_THRESHOLD = 2;
let NO_MATCH = 0xffff;
let MIN_RESULTS = 3;
let MAX_RESULTS = 500;
let UNNAMED = "<Unnamed>";
function escapeHtml(str) {
    return str.replace(/</g, "&lt;").replace(/>/g, "&gt;");
}
function getHighlightedText(item, matcher, fallbackMatcher) {
    let escapedItem = escapeHtml(item);
    let highlighted = escapedItem.replace(matcher, highlight);
    if (highlighted === escapedItem) {
        highlighted = escapedItem.replace(fallbackMatcher, highlight)
    }
    return highlighted;
}
function getURLPrefix(ui) {
    let urlPrefix="";
    let slash = "/";
    if (ui.item.category === catModules) {
        return ui.item.l + slash;
    } else if (ui.item.category === catPackages && ui.item.m) {
        return ui.item.m + slash;
    } else if (ui.item.category === catTypes || ui.item.category === catMembers) {
        if (ui.item.m) {
            urlPrefix = ui.item.m + slash;
        } else {
            $.each(packageSearchIndex, function(index, item) {
                if (item.m && ui.item.p === item.l) {
                    urlPrefix = item.m + slash;
                }
            });
        }
    }
    return urlPrefix;
}
function createSearchPattern(term) {
    let pattern = "";
    let isWordToken = false;
    term.replace(/,\s*/g, ", ").trim().split(/\s+/).forEach(function(w, index) {
        if (index > 0) {
            // whitespace between identifiers is significant
            pattern += (isWordToken && /^\w/.test(w)) ? "\\s+" : "\\s*";
        }
        let tokens = w.split(/(?=[A-Z,.()<>[\/])/);
        for (let i = 0; i < tokens.length; i++) {
            let s = tokens[i];
            if (s === "") {
                continue;
            }
            pattern += $.ui.autocomplete.escapeRegex(s);
            isWordToken =  /\w$/.test(s);
            if (isWordToken) {
                pattern += "([a-z0-9_$<>\\[\\]]*?)";
            }
        }
    });
    return pattern;
}
function createMatcher(pattern, flags) {
    let isCamelCase = /[A-Z]/.test(pattern);
    return new RegExp(pattern, flags + (isCamelCase ? "" : "i"));
}
$(function() {
    let search = $("#search-input");
    let reset = $("#reset-button");
    search.val('');
    search.prop("disabled", false);
    reset.prop("disabled", false);
    reset.click(function() {
        search.val('').focus();
    });
    search.focus();
});
$.widget("custom.catcomplete", $.ui.autocomplete, {
    _create: function() {
        this._super();
        this.widget().menu("option", "items", "> :not(.ui-autocomplete-category)");
    },
    _renderMenu: function(ul, items) {
        let rMenu = this;
        let currentCategory = "";
        rMenu.menu.bindings = $();
        $.each(items, function(index, item) {
            let li;
            if (item.category && item.category !== currentCategory) {
                ul.append("<li class=\"ui-autocomplete-category\">" + item.category + "</li>");
                currentCategory = item.category;
            }
            li = rMenu._renderItemData(ul, item);
            if (item.category) {
                li.attr("aria-label", item.category + " : " + item.l);
                li.attr("class", "result-item");
            } else {
                li.attr("aria-label", item.l);
                li.attr("class", "result-item");
            }
        });
    },
    _renderItem: function(ul, item) {
        let label;
        let matcher = createMatcher(escapeHtml(searchPattern), "g");
        let fallbackMatcher = new RegExp(fallbackPattern, "gi")
        if (item.category === catModules) {
            label = getHighlightedText(item.l, matcher, fallbackMatcher);
        } else if (item.category === catPackages) {
            label = getHighlightedText(item.l, matcher, fallbackMatcher);
        } else if (item.category === catTypes) {
            label = (item.p && item.p !== UNNAMED)
                    ? getHighlightedText(item.p + "." + item.l, matcher, fallbackMatcher)
                    : getHighlightedText(item.l, matcher, fallbackMatcher);
        } else if (item.category === catMembers) {
            label = (item.p && item.p !== UNNAMED)
                    ? getHighlightedText(item.p + "." + item.c + "." + item.l, matcher, fallbackMatcher)
                    : getHighlightedText(item.c + "." + item.l, matcher, fallbackMatcher);
        } else if (item.category === catSearchTags) {
            label = getHighlightedText(item.l, matcher, fallbackMatcher);
        } else {
            label = item.l;
        }
        let li = $("<li/>").appendTo(ul);
        let div = $("<div/>").appendTo(li);
        if (item.category === catSearchTags && item.h) {
            if (item.d) {
                div.html(label + "<span class=\"search-tag-holder-result\"> (" + item.h + ")</span><br><span class=\"search-tag-desc-result\">"
                                + item.d + "</span><br>");
            } else {
                div.html(label + "<span class=\"search-tag-holder-result\"> (" + item.h + ")</span>");
            }
        } else {
            if (item.m) {
                div.html(item.m + "/" + label);
            } else {
                div.html(label);
            }
        }
        return li;
    }
});
function rankMatch(match, category) {
    if (!match) {
        return NO_MATCH;
    }
    let index = match.index;
    let input = match.input;
    let leftBoundaryMatch = 2;
    let peripheralMatch = 0;
    // make sure match is anchored on a left word boundary
    if (index === 0 || /\W/.test(input[index - 1]) || "_" === input[index]) {
        leftBoundaryMatch = 0;
    } else if ("_" === input[index - 1] || (input[index] === input[index].toUpperCase() && !/^[A-Z0-9_$]+$/.test(input))) {
        leftBoundaryMatch = 1;
    }
    let matchEnd = index + match[0].length;
    let leftParen = input.indexOf("(");
    let endOfName = leftParen > -1 ? leftParen : input.length;
    // exclude peripheral matches
    if (category !== catModules && category !== catSearchTags) {
        let delim = category === catPackages ? "/" : ".";
        if (leftParen > -1 && leftParen < index) {
            peripheralMatch += 2;
        } else if (input.lastIndexOf(delim, endOfName) >= matchEnd) {
            peripheralMatch += 2;
        }
    }
    let delta = match[0].length === endOfName ? 0 : 1; // rank full match higher than partial match
    for (let i = 1; i < match.length; i++) {
        // lower ranking if parts of the name are missing
        if (match[i])
            delta += match[i].length;
    }
    if (category === catTypes) {
        // lower ranking if a type name contains unmatched camel-case parts
        if (/[A-Z]/.test(input.substring(matchEnd)))
            delta += 5;
        if (/[A-Z]/.test(input.substring(0, index)))
            delta += 5;
    }
    return leftBoundaryMatch + peripheralMatch + (delta / 200);

}
function doSearch(request, response) {
    let result = [];
    searchPattern = createSearchPattern(request.term);
    fallbackPattern = createSearchPattern(request.term.toLowerCase());
    if (searchPattern === "") {
        return this.close();
    }
    let camelCaseMatcher = createMatcher(searchPattern, "");
    let fallbackMatcher = new RegExp(fallbackPattern, "i");

    function searchIndexWithMatcher(indexArray, matcher, category, nameFunc) {
        if (indexArray) {
            let newResults = [];
            $.each(indexArray, function (i, item) {
                item.category = category;
                let ranking = rankMatch(matcher.exec(nameFunc(item)), category);
                if (ranking < RANKING_THRESHOLD) {
                    newResults.push({ranking: ranking, item: item});
                }
                return newResults.length <= MAX_RESULTS;
            });
            return newResults.sort(function(e1, e2) {
                return e1.ranking - e2.ranking;
            }).map(function(e) {
                return e.item;
            });
        }
        return [];
    }
    function searchIndex(indexArray, category, nameFunc) {
        let primaryResults = searchIndexWithMatcher(indexArray, camelCaseMatcher, category, nameFunc);
        result = result.concat(primaryResults);
        if (primaryResults.length <= MIN_RESULTS && !camelCaseMatcher.ignoreCase) {
            let secondaryResults = searchIndexWithMatcher(indexArray, fallbackMatcher, category, nameFunc);
            result = result.concat(secondaryResults.filter(function (item) {
                return primaryResults.indexOf(item) === -1;
            }));
        }
    }

    searchIndex(moduleSearchIndex, catModules, function(item) { return item.l; });
    searchIndex(packageSearchIndex, catPackages, function(item) {
        return (item.m && request.term.indexOf("/") > -1)
            ? (item.m + "/" + item.l) : item.l;
    });
    searchIndex(typeSearchIndex, catTypes, function(item) {
        return request.term.indexOf(".") > -1 ? item.p + "." + item.l : item.l;
    });
    searchIndex(memberSearchIndex, catMembers, function(item) {
        return request.term.indexOf(".") > -1
            ? item.p + "." + item.c + "." + item.l : item.l;
    });
    searchIndex(tagSearchIndex, catSearchTags, function(item) { return item.l; });

    if (!indexFilesLoaded()) {
        updateSearchResults = function() {
            doSearch(request, response);
        }
        result.unshift(loading);
    } else {
        updateSearchResults = function() {};
    }
    response(result);
}
$(function() {
    let expanded = false;
    let windowWidth;
    function collapse() {
        if (expanded) {
            $("div#navbar-top").removeAttr("style");
            $("button#navbar-toggle-button")
                .removeClass("expanded")
                .attr("aria-expanded", "false");
            expanded = false;
        }
    }
    $("button#navbar-toggle-button").click(function (e) {
        if (expanded) {
            collapse();
        } else {
            $("div#navbar-top").height($("#navbar-top").prop("scrollHeight"));
            $("button#navbar-toggle-button")
                .addClass("expanded")
                .attr("aria-expanded", "true");
            expanded = true;
            windowWidth = window.innerWidth;
        }
    });
    $("ul.sub-nav-list-small li a").click(collapse);
    $("input#search-input").focus(collapse);
    $("main").click(collapse);
    $(window).on("orientationchange", collapse).on("resize", function(e) {
        if (expanded && windowWidth !== window.innerWidth) collapse();
    });
    $("#search-input").catcomplete({
        minLength: 1,
        delay: 300,
        source: doSearch,
        response: function(event, ui) {
            if (!ui.content.length) {
                ui.content.push(noResult);
            } else {
                $("#search-input").empty();
            }
        },
        autoFocus: true,
        focus: function(event, ui) {
            return false;
        },
        position: {
            collision: "flip"
        },
        select: function(event, ui) {
            if (ui.item.category) {
                let url = getURLPrefix(ui);
                if (ui.item.category === catModules) {
                    url += "module-summary.html";
                } else if (ui.item.category === catPackages) {
                    if (ui.item.u) {
                        url = ui.item.u;
                    } else {
                        url += ui.item.l.replace(/\./g, '/') + "/package-summary.html";
                    }
                } else if (ui.item.category === catTypes) {
                    if (ui.item.u) {
                        url = ui.item.u;
                    } else if (ui.item.p === UNNAMED) {
                        url += ui.item.l + ".html";
                    } else {
                        url += ui.item.p.replace(/\./g, '/') + "/" + ui.item.l + ".html";
                    }
                } else if (ui.item.category === catMembers) {
                    if (ui.item.p === UNNAMED) {
                        url += ui.item.c + ".html" + "#";
                    } else {
                        url += ui.item.p.replace(/\./g, '/') + "/" + ui.item.c + ".html" + "#";
                    }
                    if (ui.item.u) {
                        url += ui.item.u;
                    } else {
                        url += ui.item.l;
                    }
                } else if (ui.item.category === catSearchTags) {
                    url += ui.item.u;
                }
                if (top !== window) {
                    parent.classFrame.location = pathtoroot + url;
                } else {
                    window.location.href = pathtoroot + url;
                }
                $("#search-input").focus();
            }
        }
    });
});
