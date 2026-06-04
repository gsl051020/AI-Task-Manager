/*
  首页昼夜主题切换 + 圆形扩散动画 + 实时时钟。

  设计说明：
  1) 主题保存在 localStorage('aitm-theme')，并在 <html data-theme> 上生效。
  2) 切换时优先使用 View Transitions API：浏览器对切换前后各拍一张快照，
     我们用 clip-path 从「点击按钮的位置」画一个由小到大的圆，
     形成「从按钮处以圆形向外延展」的昼夜切换动画。
  3) 不支持该 API 的浏览器自动降级为直接切换（CSS 过渡仍会让颜色平滑变化）。
*/
(function () {
    "use strict";

    var STORAGE_KEY = "aitm-theme";
    var root = document.documentElement;

    function currentTheme() {
        return root.getAttribute("data-theme") === "dark" ? "dark" : "light";
    }

    function applyTheme(theme) {
        root.setAttribute("data-theme", theme);
        try {
            localStorage.setItem(STORAGE_KEY, theme);
        } catch (e) {
            /* localStorage 不可用时忽略，仅本次生效 */
        }
    }

    // 以 (x, y) 为圆心、向外扩张的圆形过渡
    function circularReveal(nextTheme, x, y) {
        var supportsVT = typeof document.startViewTransition === "function";
        var reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

        if (!supportsVT || reduceMotion) {
            applyTheme(nextTheme);
            return;
        }

        // 计算从圆心到四个角的最大距离，作为圆最终半径，保证铺满整屏
        var endRadius = Math.hypot(
            Math.max(x, window.innerWidth - x),
            Math.max(y, window.innerHeight - y)
        );

        var transition = document.startViewTransition(function () {
            applyTheme(nextTheme);
        });

        transition.ready.then(function () {
            root.animate(
                {
                    clipPath: [
                        "circle(0px at " + x + "px " + y + "px)",
                        "circle(" + endRadius + "px at " + x + "px " + y + "px)"
                    ]
                },
                {
                    duration: 520,
                    easing: "cubic-bezier(0.4, 0, 0.2, 1)",
                    pseudoElement: "::view-transition-new(root)"
                }
            );
        });

        // 动画结束或中断后清理，避免 clip-path 卡住导致整页只剩左上角一小块
        function clearViewTransitionArtifacts() {
            root.style.removeProperty("clip-path");
            root.style.removeProperty("transform");
            document.body.style.removeProperty("clip-path");
            document.body.style.removeProperty("transform");
        }

        transition.finished.then(clearViewTransitionArtifacts).catch(clearViewTransitionArtifacts);
    }

    function resetPageLayoutState() {
        root.style.removeProperty("clip-path");
        root.style.removeProperty("transform");
        document.body.style.removeProperty("clip-path");
        document.body.style.removeProperty("transform");
    }

    function initToggle() {
        var btn = document.getElementById("themeToggle");
        if (!btn) {
            return;
        }
        btn.addEventListener("click", function (event) {
            var next = currentTheme() === "dark" ? "light" : "dark";
            // 圆心取按钮中心，这样圆形是从按钮处扩散出去
            var rect = btn.getBoundingClientRect();
            var x = rect.left + rect.width / 2;
            var y = rect.top + rect.height / 2;
            circularReveal(next, x, y);
        });
    }

    /*
      交互卡片：模拟眼随/光随效果。
      鼠标移动时更新卡片内部高光位置，并附带轻微 3D 倾斜。
    */
    function initInteractiveCards() {
        var reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        if (reduceMotion) {
            return;
        }
        var cards = Array.prototype.slice.call(document.querySelectorAll(".interactive-card"));
        cards.forEach(function (card) {
            card.addEventListener("pointermove", function (event) {
                var rect = card.getBoundingClientRect();
                var x = ((event.clientX - rect.left) / rect.width) * 100;
                var y = ((event.clientY - rect.top) / rect.height) * 100;

                var rotateY = ((x - 50) / 50) * 4;
                var rotateX = -((y - 50) / 50) * 4;

                card.style.setProperty("--mx", x.toFixed(2) + "%");
                card.style.setProperty("--my", y.toFixed(2) + "%");
                card.style.setProperty("--glow", "1");
                card.style.transform =
                    "perspective(900px) rotateX(" + rotateX.toFixed(2) + "deg) rotateY(" + rotateY.toFixed(2) + "deg)";
            });

            card.addEventListener("pointerleave", function () {
                card.style.setProperty("--glow", "0");
                card.style.transform = "";
            });
        });
    }

    /*
      眼球跟随组件：
      按 Figma Make 示例思路，用鼠标方向向量驱动瞳孔位移，并限制在眼白内部。
    */
    function initEyeFollowWidgets() {
        var eyes = Array.prototype.slice.call(document.querySelectorAll(".eye-follow-widget .eye"));
        if (eyes.length === 0) {
            return;
        }
        var reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        if (reduceMotion) {
            return;
        }

        var configs = eyes.map(function (eye) {
            var pupil = eye.querySelector(".pupil");
            var defaultX = parseFloat(eye.getAttribute("data-default-x") || "0");
            var defaultY = parseFloat(eye.getAttribute("data-default-y") || "0");
            return {
                eye: eye,
                pupil: pupil,
                defaultX: defaultX,
                defaultY: defaultY
            };
        });

        function renderPupil(config, x, y) {
            if (!config.pupil) {
                return;
            }
            config.pupil.style.transform =
                "translate(-50%, -50%) translate(" + x.toFixed(2) + "px, " + y.toFixed(2) + "px)";
        }

        // 首次渲染默认表情
        configs.forEach(function (c) {
            renderPupil(c, c.defaultX, c.defaultY);
        });

        window.addEventListener("mousemove", function (event) {
            configs.forEach(function (config) {
                var rect = config.eye.getBoundingClientRect();
                var eyeCenterX = rect.left + rect.width / 2;
                var eyeCenterY = rect.top + rect.height / 2;
                var dx = event.clientX - eyeCenterX;
                var dy = event.clientY - eyeCenterY;
                var distance = Math.sqrt(dx * dx + dy * dy);
                if (distance < 1) {
                    renderPupil(config, config.defaultX, config.defaultY);
                    return;
                }

                var eyeRadius = rect.width / 2;
                var pupilRadius = (config.pupil ? config.pupil.offsetWidth : 20) / 2;
                var maxMovement = Math.max(eyeRadius - pupilRadius - 3, 4);
                var nx = dx / distance;
                var ny = dy / distance;
                var moveX = Math.min(distance, maxMovement) * nx + config.defaultX;
                var moveY = Math.min(distance, maxMovement) * ny + config.defaultY;
                var deltaX = moveX - config.defaultX;
                var deltaY = moveY - config.defaultY;
                var deltaDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                if (deltaDistance > maxMovement) {
                    var scale = maxMovement / deltaDistance;
                    moveX = config.defaultX + deltaX * scale;
                    moveY = config.defaultY + deltaY * scale;
                }
                renderPupil(config, moveX, moveY);
            });
        });
    }

    /*
      拟真翻页时钟：
      每个数字是一个「翻页单元」，包含上下两个静态半卡 + 两片会翻动的叶片。
      数字变化时：上叶片（旧数字）绕底边向下翻走，露出新数字的上半；
      随后下叶片（新数字）绕顶边翻入，盖住旧数字的下半，最终显示新数字。
    */
    function createFlipUnit() {
        var el = document.createElement("div");
        el.className = "flip-unit";
        el.innerHTML =
            '<div class="flip-card card-upper"><span class="flip-num">0</span></div>' +
            '<div class="flip-card card-lower"><span class="flip-num">0</span></div>' +
            '<div class="flip-card card-flip-front"><span class="flip-num">0</span></div>' +
            '<div class="flip-card card-flip-back"><span class="flip-num">0</span></div>';

        var upper = el.querySelector(".card-upper .flip-num");
        var lower = el.querySelector(".card-lower .flip-num");
        var front = el.querySelector(".card-flip-front .flip-num");
        var back = el.querySelector(".card-flip-back .flip-num");
        var current = null;

        // 下叶片翻入结束后，把底部静态卡也更新为新数字并复位
        el.addEventListener("animationend", function (e) {
            if (e.animationName === "flipBackIn") {
                lower.textContent = upper.textContent;
                el.classList.remove("flipping");
            }
        });

        function set(value) {
            if (value === current) {
                return;
            }
            var old = current === null ? value : current;
            upper.textContent = value; // 顶部静态卡：直接显示新数字（被旧叶片盖住，翻走后露出）
            front.textContent = old;   // 上叶片：显示旧数字，向下翻走
            lower.textContent = old;   // 底部静态卡：暂留旧数字
            back.textContent = value;  // 下叶片：显示新数字，翻入盖住旧的

            if (current === null) {
                // 首次渲染：不播放动画，直接落到当前值
                lower.textContent = value;
                current = value;
                return;
            }
            el.classList.remove("flipping");
            void el.offsetWidth; // 强制回流，确保动画可重复触发
            el.classList.add("flipping");
            current = value;
        }

        return { el: el, set: set };
    }

    function initClock() {
        var containers = Array.prototype.slice.call(document.querySelectorAll(".flip-clock"));
        if (containers.length === 0) {
            return;
        }

        var weekdays = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];

        function pad(n) {
            return n < 10 ? "0" + n : "" + n;
        }

        function buildClock(container) {
            var format = container.getAttribute("data-clock-format") || "hms";
            var groups = format === "hm" ? [2, 2] : [2, 2, 2];
            var units = [];

            container.innerHTML = "";
            groups.forEach(function (count, gi) {
                var group = document.createElement("div");
                group.className = "flip-group";
                for (var i = 0; i < count; i++) {
                    var unit = createFlipUnit();
                    group.appendChild(unit.el);
                    units.push(unit);
                }
                container.appendChild(group);
                if (gi < groups.length - 1) {
                    var sep = document.createElement("span");
                    sep.className = "flip-sep";
                    sep.textContent = ":";
                    container.appendChild(sep);
                }
            });

            var dateEl = null;
            var host = container.closest(".clock-card, .workspace-widget");
            if (host) {
                dateEl = host.querySelector("[data-clock-date]");
            }
            return { units: units, format: format, dateEl: dateEl };
        }

        var clocks = containers.map(buildClock);

        function tick() {
            var now = new Date();
            var h = pad(now.getHours());
            var m = pad(now.getMinutes());
            var s = pad(now.getSeconds());

            clocks.forEach(function (clock) {
                var digits = clock.format === "hm" ? h + m : h + m + s;
                for (var i = 0; i < clock.units.length; i++) {
                    clock.units[i].set(digits.charAt(i));
                }
                if (clock.dateEl) {
                    if (clock.format === "hm") {
                        clock.dateEl.textContent =
                            now.getFullYear() + "/" + pad(now.getMonth() + 1) + "/" + pad(now.getDate());
                    } else {
                        clock.dateEl.textContent =
                            now.getFullYear() + " / " + pad(now.getMonth() + 1) + " / " +
                            pad(now.getDate()) + " · " + weekdays[now.getDay()];
                    }
                }
            });
        }

        tick();
        setInterval(tick, 1000);
    }

    /*
      鼠标跟随水波背景：
      - 全屏 canvas，每帧先画「主题底色渐变」，再画鼠标划过生成的扩散波纹圆环。
      - 颜色不直接硬切：维护「当前色 → 目标色」并逐帧插值（lerp），
        主题切换时背景会自然过渡到对应配色（白天灰白+浅蓝 / 黑夜深灰+紫黑）。
      - prefers-reduced-motion 时只画静态渐变、不响应鼠标，避免动效干扰。
    */
    function initRippleBackground() {
        var canvas = document.getElementById("rippleBg");
        if (!canvas) {
            return;
        }
        var ctx = canvas.getContext("2d");
        if (!ctx) {
            return;
        }

        var reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        var dpr = Math.min(window.devicePixelRatio || 1, 2);
        var width = 0;
        var height = 0;

        function resize() {
            width = window.innerWidth;
            height = window.innerHeight;
            canvas.width = Math.round(width * dpr);
            canvas.height = Math.round(height * dpr);
            canvas.style.width = width + "px";
            canvas.style.height = height + "px";
            ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
        }
        resize();
        window.addEventListener("resize", resize);

        // 把 "#rrggbb" 解析为 [r, g, b]
        function hexToRgb(hex) {
            var clean = (hex || "").trim().replace("#", "");
            if (clean.length === 3) {
                clean = clean.charAt(0) + clean.charAt(0) + clean.charAt(1) +
                    clean.charAt(1) + clean.charAt(2) + clean.charAt(2);
            }
            var num = parseInt(clean, 16);
            if (isNaN(num)) {
                return [30, 30, 36];
            }
            return [(num >> 16) & 255, (num >> 8) & 255, num & 255];
        }

        // 把 "120, 170, 220" 解析为 [r, g, b]
        function parseRgbTriple(value, fallback) {
            var parts = (value || "").split(",");
            if (parts.length < 3) {
                return fallback;
            }
            var r = parseFloat(parts[0]);
            var g = parseFloat(parts[1]);
            var b = parseFloat(parts[2]);
            if (isNaN(r) || isNaN(g) || isNaN(b)) {
                return fallback;
            }
            return [r, g, b];
        }

        // 从 CSS 变量读取当前主题的目标配色：bg=背景色，dot=波点色
        function readTargetColors() {
            var styles = getComputedStyle(root);
            return {
                bg: hexToRgb(styles.getPropertyValue("--dot-bg") || "#ffffff"),
                dot: parseRgbTriple(styles.getPropertyValue("--dot-color"), [205, 209, 216])
            };
        }

        var target = readTargetColors();
        // 当前色初始化为目标色，避免首帧闪烁
        var current = {
            bg: target.bg.slice(),
            dot: target.dot.slice()
        };

        function lerpTriple(cur, dst, t) {
            cur[0] += (dst[0] - cur[0]) * t;
            cur[1] += (dst[1] - cur[1]) * t;
            cur[2] += (dst[2] - cur[2]) * t;
        }

        function rgbCss(triple) {
            return "rgb(" + Math.round(triple[0]) + "," + Math.round(triple[1]) + "," + Math.round(triple[2]) + ")";
        }

        // 主题变化时刷新目标色（点击切换按钮后会改 data-theme）
        var observer = new MutationObserver(function () {
            target = readTargetColors();
        });
        observer.observe(root, { attributes: true, attributeFilter: ["data-theme"] });

        /*
          波点（点阵）背景参数：
          - GAP：相邻波点间距；BASE_R：静态基础半径。
          - 静态呼吸：每个点按自身位置错开相位做 sin 脉动，半径在基础值上下起伏 → 整片点阵有呼吸波动感。
          - 鼠标交互：离鼠标越近的点半径越大（距离衰减），形成「鼠标处波点放大」的聚焦效果。
        */
        var GAP = 30;
        var BASE_R = 2.2;
        var BREATHE_AMP = 2.6;       // 呼吸幅度（相对基础半径，越大起伏越明显）
        var MOUSE_RADIUS = 150;      // 鼠标影响半径（像素）
        var MOUSE_GROW = 4.0;        // 鼠标处波点最大额外放大（像素）

        // 鼠标位置：初始放到屏幕外，避免一进页面中心就有放大点
        var mouseX = -9999;
        var mouseY = -9999;
        if (!reduceMotion) {
            window.addEventListener("mousemove", function (event) {
                mouseX = event.clientX;
                mouseY = event.clientY;
            });
            // 鼠标移出窗口时复位，让放大效果消失
            window.addEventListener("mouseout", function (event) {
                if (!event.relatedTarget) {
                    mouseX = -9999;
                    mouseY = -9999;
                }
            });
        }

        function drawBackground() {
            ctx.fillStyle = rgbCss(current.bg);
            ctx.fillRect(0, 0, width, height);
        }

        // 绘制点阵：背景纯色 + 规则波点（呼吸脉动 + 鼠标处放大）
        function drawDots(timeSec) {
            var dr = Math.round(current.dot[0]);
            var dg = Math.round(current.dot[1]);
            var db = Math.round(current.dot[2]);
            var dotPrefix = "rgba(" + dr + "," + dg + "," + db + ",";
            // 居中铺点，使网格在不同尺寸下两侧留白均匀
            var startX = (width % GAP) / 2;
            var startY = (height % GAP) / 2;

            for (var y = startY; y <= height; y += GAP) {
                for (var x = startX; x <= width; x += GAP) {
                    // 呼吸：相位随坐标错开，整片点阵像波浪一样起伏
                    var phase = x * 0.02 + y * 0.02 + timeSec * 1.4;
                    var breathe = (Math.sin(phase) + 1) / 2; // 0..1
                    var radius = BASE_R * (1 - BREATHE_AMP / 2 + breathe * BREATHE_AMP);
                    if (radius < 0.2) { radius = 0.2; }
                    var alpha = 0.18 + breathe * 0.72; // 明暗对比也随呼吸加强

                    // 鼠标交互：近鼠标的点变大、变亮
                    if (!reduceMotion) {
                        var ddx = x - mouseX;
                        var ddy = y - mouseY;
                        var dist = Math.sqrt(ddx * ddx + ddy * ddy);
                        if (dist < MOUSE_RADIUS) {
                            var f = 1 - dist / MOUSE_RADIUS; // 1（最近）→0（边缘）
                            f = f * f;                        // 缓动，使中心更突出
                            radius += MOUSE_GROW * f;
                            alpha = Math.min(1, alpha + 0.4 * f);
                        }
                    }

                    ctx.beginPath();
                    ctx.arc(x, y, radius, 0, Math.PI * 2);
                    ctx.fillStyle = dotPrefix + alpha.toFixed(3) + ")";
                    ctx.fill();
                }
            }
        }

        function frame() {
            // 背景色与波点色平滑过渡（约 0.12/帧 ≈ 0.3~0.4s 完成切换）
            lerpTriple(current.bg, target.bg, 0.12);
            lerpTriple(current.dot, target.dot, 0.12);

            drawBackground();
            drawDots(performance.now() / 1000);

            requestAnimationFrame(frame);
        }

        if (reduceMotion) {
            // 静态：只画一次纯色背景 + 静态点阵（不呼吸、不响应鼠标）
            drawBackground();
            drawDots(0);
            return;
        }
        requestAnimationFrame(frame);
    }

    function bootTheme() {
        resetPageLayoutState();
        initToggle();
        initClock();
        initInteractiveCards();
        initEyeFollowWidgets();
        initRippleBackground();
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", bootTheme);
    } else {
        bootTheme();
    }
})();
