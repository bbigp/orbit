//  动态添加代码名称和复制按钮
//  highlight-operate-container.js
//  Brew
//
//  Created by Duyb on 2025/7/14.
//

document.addEventListener('DOMContentLoaded', function () {
  formatBrewPage()
  listenThemeChange();
});

function formatBrewPage() {
  listenImgStatus()
  formatTable();
  formatPreCode();

  // 调用 Highlight.js 进行高亮
  hljs.highlightAll();

  createCodeOperateContainer();
}

/// 将 <pre><code> 元素包裹进 <div class="br-code-container">
function formatPreCode() {
  const codeBlocks = document.querySelectorAll('pre > code');
  const className = 'br-code-container';

  codeBlocks.forEach(originalCode => {
    const pre = originalCode.parentElement; // 原始 <pre>

    if (pre.parentElement && pre.parentElement.classList.contains(className)) {
      return;
    }

    const originalContent = originalCode.textContent; // 获取原始 code 内容

    // 创建新的 <code>
    const newCode = document.createElement('code');
    newCode.textContent = originalContent; // 设置为原始 content

    // 创建新的 <pre> 并放入新 <code>
    const newPre = document.createElement('pre');
    newPre.appendChild(newCode);

    // 创建容器
    const container = document.createElement('div');
    container.className = className;

    // 替换旧的 pre
    if (pre.parentNode) {
      pre.parentNode.replaceChild(container, pre);
    }

    // 把新的 pre 插入到容器中
    container.appendChild(newPre);
  });
}

// svg 使用内联方式才支持 currentColor
let SVG_COPY = `<svg class="copy-btn-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
<path fill-rule="evenodd" clip-rule="evenodd" d="M13.8 3.68007C14.3713 3.68007 14.7588 3.6806 15.0582 3.70506C15.3497 3.72887 15.4972 3.77193 15.5993 3.82394C15.8476 3.95049 16.0496 4.15243 16.1761 4.4008C16.2281 4.50288 16.2712 4.65039 16.295 4.94185C16.3195 5.24124 16.32 5.6288 16.32 6.20007V11.0001C16.32 11.4753 16.3196 11.7977 16.3026 12.0481C16.2859 12.2924 16.2556 12.4181 16.2195 12.5052C16.0856 12.8286 15.8286 13.0856 15.5051 13.2196C15.418 13.2557 15.2923 13.2859 15.048 13.3026C14.9407 13.3099 14.8202 13.3142 14.68 13.3167V9.17209V9.17206C14.68 8.63582 14.68 8.19244 14.6505 7.8311C14.6198 7.45554 14.5539 7.10912 14.3879 6.78337C14.131 6.2791 13.721 5.86911 13.2167 5.61217C12.891 5.44619 12.5445 5.38026 12.169 5.34958C11.8076 5.32005 11.3643 5.32006 10.828 5.32007H10.828H6.68342C6.68589 5.17992 6.69014 5.05937 6.69746 4.95205C6.71413 4.70776 6.74441 4.58203 6.78049 4.49493C6.91446 4.17149 7.17143 3.91452 7.49487 3.78055C7.58197 3.74447 7.70769 3.71419 7.95198 3.69752C8.20238 3.68044 8.52476 3.68007 9.00001 3.68007H13.8ZM5.32315 5.32599C5.32589 5.15531 5.33102 5.00015 5.34062 4.85947C5.36201 4.54593 5.40785 4.25492 5.52401 3.97448C5.79601 3.3178 6.31774 2.79608 6.97442 2.52407C7.25485 2.40791 7.54587 2.36207 7.85941 2.34068C8.1616 2.32006 8.53056 2.32006 8.97674 2.32007H8.97677H13.828H13.828C14.3643 2.32006 14.8076 2.32005 15.169 2.34958C15.5445 2.38026 15.891 2.44619 16.2167 2.61217C16.721 2.86911 17.131 3.2791 17.3879 3.78337C17.5539 4.10912 17.6198 4.45554 17.6505 4.8311C17.68 5.19245 17.68 5.63582 17.68 6.17206V6.17209V11.0233C17.68 11.4695 17.68 11.8385 17.6594 12.1407C17.638 12.4542 17.5922 12.7452 17.476 13.0257C17.204 13.6823 16.6823 14.2041 16.0256 14.4761C15.7452 14.5922 15.4541 14.6381 15.1406 14.6595C14.9999 14.6691 14.8448 14.6742 14.6741 14.6769C14.6699 14.8556 14.6627 15.0193 14.6505 15.169C14.6198 15.5446 14.5539 15.891 14.3879 16.2168C14.131 16.721 13.721 17.131 13.2167 17.388C12.891 17.5539 12.5445 17.6199 12.169 17.6506C11.8076 17.6801 11.3643 17.6801 10.828 17.6801H10.828H6.17203H6.172C5.63576 17.6801 5.19238 17.6801 4.83104 17.6506C4.45548 17.6199 4.10906 17.5539 3.78331 17.388C3.27904 17.131 2.86905 16.721 2.61211 16.2168C2.44613 15.891 2.3802 15.5446 2.34951 15.169C2.31999 14.8077 2.32 14.3643 2.32001 13.8281V13.8281V9.17207V9.17205C2.32 8.63581 2.31999 8.19244 2.34951 7.8311C2.3802 7.45554 2.44613 7.10912 2.61211 6.78337C2.86905 6.2791 3.27904 5.86911 3.78331 5.61217C4.10906 5.44619 4.45548 5.38026 4.83104 5.34958C4.98074 5.33734 5.14452 5.33018 5.32315 5.32599ZM4.40074 6.82394C4.50281 6.77193 4.65033 6.72887 4.94179 6.70506C5.24118 6.6806 5.62873 6.68007 6.20001 6.68007H10.8C11.3713 6.68007 11.7588 6.6806 12.0582 6.70506C12.3497 6.72887 12.4972 6.77193 12.5993 6.82394C12.8476 6.95049 13.0496 7.15243 13.1761 7.4008C13.2281 7.50288 13.2712 7.65039 13.295 7.94185C13.3195 8.24124 13.32 8.6288 13.32 9.20007V13.8001C13.32 14.3713 13.3195 14.7589 13.295 15.0583C13.2712 15.3497 13.2281 15.4973 13.1761 15.5993C13.0496 15.8477 12.8476 16.0496 12.5993 16.1762C12.4972 16.2282 12.3497 16.2713 12.0582 16.2951C11.7588 16.3195 11.3713 16.3201 10.8 16.3201H6.20001C5.62873 16.3201 5.24118 16.3195 4.94179 16.2951C4.65033 16.2713 4.50281 16.2282 4.40074 16.1762C4.15237 16.0496 3.95043 15.8477 3.82388 15.5993C3.77187 15.4973 3.72881 15.3497 3.705 15.0583C3.68054 14.7589 3.68001 14.3713 3.68001 13.8001V9.20007C3.68001 8.6288 3.68054 8.24124 3.705 7.94185C3.72881 7.65039 3.77187 7.50288 3.82388 7.4008C3.95043 7.15243 4.15237 6.95049 4.40074 6.82394Z" fill="currentColor" style="fill-opacity:1;"/>
</svg>
`;

let SVG_CHECK = `<svg class="copy-btn-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
<path fill-rule="evenodd" clip-rule="evenodd" d="M16.6404 4.19107C17.0437 4.50124 17.1191 5.07958 16.8089 5.48281L9.41013 16.2089C9.25549 16.41 9.02479 16.5384 8.77244 16.5638C8.52009 16.5893 8.2684 16.5095 8.07674 16.3434L3.31787 11.9319C2.93343 11.5987 2.89188 11.0169 3.22506 10.6325C3.55824 10.2481 4.13999 10.2065 4.52443 10.5397L8.54532 14.3116L15.3487 4.35956C15.6589 3.95632 16.2372 3.88089 16.6404 4.19107Z" fill="currentColor" style="fill-opacity:1;"/>
</svg>
`;
// 添加 .code-header 操作栏（语言名 + 复制按钮）
function createCodeOperateContainer() {
  const containers = document.querySelectorAll('.br-code-container');

  containers.forEach(container => {
    const code = container.querySelector('pre > code');
    const langMatch = code.className.match(/language-(\w+)/);
    const langName = langMatch ? langMatch[1] : '';

    // 创建 header
    const header = document.createElement('div');
    header.className = 'code-header';

    const langSpan = document.createElement('span');
    langSpan.className = 'lang-name';
    langSpan.textContent = `Code${langName ? ` · ${langName}` : ''}`;

    const copyBtn = document.createElement('button');
    copyBtn.className = 'copy-btn';
    copyBtn.innerHTML = `<div class="svg-color-container">${SVG_COPY}</div><span class="copy-btn-text">Copy</span>`;

    // 绑定复制事件
    copyBtn.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      navigator.clipboard
        .writeText(code.textContent)
        .then(() => {
          //        copyBtn.textContent = 'Copied';
        })
        .catch(err => {
          console.error('Copy failed:', err);
        });

      // 动效
      const iconContainer = copyBtn.querySelector('.svg-color-container');
      const text = copyBtn.querySelector('.copy-btn-text');

      // 缩小
      copyBtn.style.transform = 'scale(0.5)';

      setTimeout(() => {
        // 替换内容
        iconContainer.innerHTML = SVG_CHECK;
        text.textContent = 'Copied';
        // 放大恢复
        copyBtn.style.transform = 'scale(1)';
      }, 120);

      // 3秒后恢复默认状态
      setTimeout(() => {
        iconContainer.innerHTML = SVG_COPY;
        text.textContent = 'Copy';
      }, 3000);
    });

    header.appendChild(langSpan);
    header.appendChild(copyBtn);

    // 插入到 container 的最前面
    container.insertBefore(header, container.firstChild);
  });
}

function setTheme(theme) {
  document.body.setAttribute('data-theme', theme);
}

function listenThemeChange() {
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');

  // 监听系统主题变化
  if (typeof mediaQuery.addEventListener === 'function') {
    mediaQuery.addEventListener('change', event => {
      const newTheme = event.matches ? 'dark' : 'light';
      setTheme(newTheme);
    });
  } else {
    // 兼容旧版 Safari
    mediaQuery.addListener(event => {
      const newTheme = event.matches ? 'dark' : 'light';
      setTheme(newTheme);
    });
  }
}

// 自定义 css: table 添加父元素 .br-table-container
function formatTable() {
  const className = 'br-table-container';
  const scrollClass = 'br-table-scroll';
  const innerClass = 'br-table-inner';

  document.querySelectorAll('table').forEach(table => {
    const existingContainer = table.closest(`.${className}`);

    if (existingContainer) {
      bindTableRightMask(existingContainer);
      return;
    }

    const container = document.createElement('div');
    container.className = className;

    const scroll = document.createElement('div');
    scroll.className = scrollClass;

    const inner = document.createElement('div');
    inner.className = innerClass;

    table.parentNode.replaceChild(container, table);
    container.appendChild(scroll);
    scroll.appendChild(inner);
    inner.appendChild(table);
    bindTableRightMask(container);
  });
}

function updateTableRightMask(container) {
  if (!container) return;

  const scroller =
    container.querySelector('.br-table-scroll') ||
    container.querySelector('.br-table-inner') ||
    container;
  const maxScrollLeft = scroller.scrollWidth - scroller.clientWidth;
  const hasRightOverflow = maxScrollLeft > 1 && scroller.scrollLeft < maxScrollLeft - 1;

  container.classList.toggle('show-right-mask', hasRightOverflow);
}

function bindTableRightMask(container) {
  if (!container || container.dataset.maskBound === '1') {
    updateTableRightMask(container);
    return;
  }

  const update = () => updateTableRightMask(container);
  const scroller =
    container.querySelector('.br-table-scroll') ||
    container.querySelector('.br-table-inner') ||
    container;
  scroller.addEventListener('scroll', update, { passive: true });
  window.addEventListener('resize', update);

  if (typeof ResizeObserver === 'function') {
    const observer = new ResizeObserver(update);
    observer.observe(container);
    observer.observe(scroller);
  }

  container.dataset.maskBound = '1';
  requestAnimationFrame(update);
}

// 自定义 css: img 下载失败添加 error 类
function listenImgStatus() {
  document.querySelectorAll('img').forEach(handleImageStatus);
}

const processedImages = new WeakSet();

function handleImageStatus(img) {
  if (processedImages.has(img)) return;

  img.addEventListener('error', () => {
    img.classList.add('error');
  });

  img.addEventListener('load', () => {
    img.classList.remove('error');
  });

  processedImages.add(img);
}
