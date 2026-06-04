/*
  首页 Hero 打字机动画（独立脚本，避免 theme.js 被浏览器缓存后看不到效果）。
  仅 index.html 引入；顺序：标题两行 → 简介 → 显示按钮。
*/
(function () {
    "use strict";

    function getText(el) {
        var fromAttr = el.getAttribute("data-type-text");
        if (fromAttr) {
            return fromAttr.replace(/\s+/g, "").trim();
        }
        return el.textContent.replace(/\s+/g, "").trim();
    }

    function initHeroTypewriter() {
        var hero = document.querySelector(".hero[data-typewriter]");
        if (!hero || hero.getAttribute("data-tw-done") === "1") {
            return;
        }

        var reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        var queue = [];
        hero.querySelectorAll(".hero-title .type-line").forEach(function (el) {
            queue.push({
                el: el,
                text: getText(el),
                speed: reduceMotion ? 12 : 88,
                pauseAfter: reduceMotion ? 80 : 380
            });
        });
        var subtitle = hero.querySelector(".hero-subtitle");
        if (subtitle) {
            queue.push({
                el: subtitle,
                text: getText(subtitle),
                speed: reduceMotion ? 6 : 32,
                pauseAfter: 0
            });
        }
        if (queue.length === 0) {
            hero.classList.add("hero--typed");
            revealFeatureTiles();
            return;
        }

        var cursor = document.createElement("span");
        cursor.className = "type-cursor";
        cursor.setAttribute("aria-hidden", "true");

        queue.forEach(function (item) {
            item.el.textContent = "";
        });
        hero.classList.add("hero--typing");
        hero.classList.remove("hero--typed");

        function placeCursor(el) {
            if (cursor.parentNode) {
                cursor.parentNode.removeChild(cursor);
            }
            el.appendChild(cursor);
        }

        function typeLine(item) {
            return new Promise(function (resolve) {
                var i = 0;
                placeCursor(item.el);
                function step() {
                    if (i < item.text.length) {
                        item.el.insertBefore(document.createTextNode(item.text.charAt(i)), cursor);
                        i += 1;
                        setTimeout(step, item.speed);
                        return;
                    }
                    setTimeout(resolve, item.pauseAfter);
                }
                step();
            });
        }

        (function run(index) {
            if (index >= queue.length) {
                if (cursor.parentNode) {
                    cursor.parentNode.removeChild(cursor);
                }
                hero.classList.remove("hero--typing");
                hero.classList.add("hero--typed");
                hero.setAttribute("data-tw-done", "1");
                revealFeatureTiles();
                return;
            }
            typeLine(queue[index]).then(function () {
                run(index + 1);
            });
        })(0);
    }

    function revealFeatureTiles() {
        var features = document.querySelector(".features[data-feature-reveal]");
        if (!features) {
            return;
        }
        var reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        var delay = reduceMotion ? 0 : 200;

        function play() {
            features.classList.remove("features--revealed");
            void features.offsetWidth;
            features.classList.add("features--revealed");
        }

        setTimeout(play, delay);
    }

    function resetFeatureTiles() {
        var features = document.querySelector(".features[data-feature-reveal]");
        if (features) {
            features.classList.remove("features--revealed");
        }
    }

    function boot() {
        resetFeatureTiles();
        initHeroTypewriter();
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", boot);
    } else {
        boot();
    }

    window.addEventListener("pageshow", function (event) {
        if (event.persisted) {
            var hero = document.querySelector(".hero[data-typewriter]");
            if (hero) {
                hero.removeAttribute("data-tw-done");
            }
            resetFeatureTiles();
            boot();
        }
    });
})();
