const MIME_TYPE = {
  image: 'image',
  video: 'video',
  audio: 'audio',
  gifv: 'gifv',
};

const SVG_PLAY = `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 20 20" fill="currentColor">
<path d="M14.7296 7.90006C15.863 8.595 16.4297 8.94247 16.6233 9.38903C16.7922 9.77894 16.7922 10.2211 16.6233 10.611C16.4297 11.0575 15.863 11.405 14.7296 12.0999L8.52163 15.9064C7.2893 16.662 6.67314 17.0398 6.16523 16.9967C5.72245 16.959 5.31731 16.7335 5.05308 16.3775C4.75 15.9691 4.75 15.2482 4.75 13.8065L4.75 6.19352C4.75 4.75178 4.75 4.03091 5.05308 3.62254C5.31731 3.26653 5.72245 3.04095 6.16523 3.00332C6.67314 2.96016 7.2893 3.33797 8.52163 4.09358L14.7296 7.90006Z" fill="currentColor" />
</svg>`;

const SVG_PAUSE = `<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 20 20" fill="currentColor">
<path fill-rule="evenodd" clip-rule="evenodd" d="M4.16349 3.31901C4 3.63988 4 4.05992 4 4.9V15.1C4 15.9401 4 16.3601 4.16349 16.681C4.3073 16.9632 4.53677 17.1927 4.81901 17.3365C5.13988 17.5 5.55992 17.5 6.4 17.5H6.6C7.44008 17.5 7.86012 17.5 8.18099 17.3365C8.46323 17.1927 8.6927 16.9632 8.83651 16.681C9 16.3601 9 15.9401 9 15.1V4.9C9 4.05992 9 3.63988 8.83651 3.31901C8.6927 3.03677 8.46323 2.8073 8.18099 2.66349C7.86012 2.5 7.44008 2.5 6.6 2.5H6.4C5.55992 2.5 5.13988 2.5 4.81901 2.66349C4.53677 2.8073 4.3073 3.03677 4.16349 3.31901ZM11.1635 3.31901C11 3.63988 11 4.05992 11 4.9V15.1C11 15.9401 11 16.3601 11.1635 16.681C11.3073 16.9632 11.5368 17.1927 11.819 17.3365C12.1399 17.5 12.5599 17.5 13.4 17.5H13.6C14.4401 17.5 14.8601 17.5 15.181 17.3365C15.4632 17.1927 15.6927 16.9632 15.8365 16.681C16 16.3601 16 15.9401 16 15.1V4.9C16 4.05992 16 3.63988 15.8365 3.31901C15.6927 3.03677 15.4632 2.8073 15.181 2.66349C14.8601 2.5 14.4401 2.5 13.6 2.5H13.4C12.5599 2.5 12.1399 2.5 11.819 2.66349C11.5368 2.8073 11.3073 3.03677 11.1635 3.31901Z" fill="currentColor" />
</svg>`;

const SVG_LOADING = `<svg class="br-icon-spinner" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" width="18" height="18" viewBox="0 0 24.6875 24.4238">
<g>
  <rect height="24.4238" opacity="0" width="24.6875" x="0" y="0" />
  <path class="spinner-dot-1" d="M8.85742 8.86719C9.46289 8.26172 9.46289 7.26562 8.85742 6.66016L5.77148 3.56445C5.15625 2.95898 4.16992 2.95898 3.56445 3.56445C2.95898 4.16992 2.95898 5.15625 3.56445 5.77148L6.66016 8.86719C7.26562 9.46289 8.25195 9.46289 8.85742 8.86719Z" fill="currentColor" />
  <path class="spinner-dot-2" d="M7.49023 12.168C7.49023 11.3086 6.78711 10.6152 5.92773 10.6152L1.55273 10.6152C0.703125 10.6152 0 11.3086 0 12.168C0 13.0273 0.703125 13.7305 1.55273 13.7305L5.92773 13.7305C6.78711 13.7305 7.49023 13.0273 7.49023 12.168Z" fill="currentColor" />
  <path class="spinner-dot-3" d="M8.85742 15.4785C8.25195 14.873 7.26562 14.873 6.66016 15.4785L3.56445 18.5645C2.95898 19.1797 2.95898 20.166 3.56445 20.7715C4.16992 21.377 5.15625 21.377 5.77148 20.7715L8.85742 17.6758C9.46289 17.0703 9.46289 16.0742 8.85742 15.4785Z" fill="currentColor" />
  <path class="spinner-dot-4" d="M12.168 16.8457C11.3086 16.8457 10.6055 17.5488 10.6055 18.3984L10.6055 22.7734C10.6055 23.6328 11.3086 24.3359 12.168 24.3359C13.0176 24.3359 13.7207 23.6328 13.7207 22.7734L13.7207 18.3984C13.7207 17.5488 13.0176 16.8457 12.168 16.8457Z" fill="currentColor" />
  <path class="spinner-dot-5" d="M15.4688 15.4785C14.8633 16.0742 14.8633 17.0703 15.4688 17.6758L18.5547 20.7715C19.1699 21.377 20.166 21.377 20.7715 20.7715C21.3672 20.166 21.3672 19.1797 20.7715 18.5645L17.666 15.4785C17.0703 14.873 16.0742 14.873 15.4688 15.4785Z" fill="currentColor" />
  <path class="spinner-dot-6" d="M16.8359 12.168C16.8359 13.0273 17.5391 13.7305 18.3984 13.7305L22.7734 13.7305C23.6328 13.7305 24.3262 13.0273 24.3262 12.168C24.3262 11.3086 23.6328 10.6152 22.7734 10.6152L18.3984 10.6152C17.5391 10.6152 16.8359 11.3086 16.8359 12.168Z" fill="currentColor" />
  <path class="spinner-dot-7" d="M15.4688 8.86719C16.0742 9.46289 17.0703 9.46289 17.666 8.86719L20.7715 5.77148C21.3672 5.15625 21.3672 4.16992 20.7715 3.56445C20.166 2.95898 19.1699 2.95898 18.5547 3.56445L15.4688 6.66016C14.8633 7.26562 14.8633 8.26172 15.4688 8.86719Z" fill="currentColor" />
  <path class="spinner-dot-8" d="M12.168 7.49023C13.0176 7.49023 13.7207 6.78711 13.7207 5.9375L13.7207 1.5625C13.7207 0.703125 13.0176 0 12.168 0C11.3086 0 10.6055 0.703125 10.6055 1.5625L10.6055 5.9375C10.6055 6.78711 11.3086 7.49023 12.168 7.49023Z" fill="currentColor" />
</g>
</svg>`;

// audio 样式覆盖 & AudioPublsher 播放
function formatAudio() {
  const audios = document.querySelectorAll(
    'audio[src]:not([src=""]), audio:has(source[src]:not([src=""]))'
  );

  audios.forEach(audio => {
    if (audio.dataset.processed === 'true') return;
    audio.dataset.processed = 'true';

    let src = audio.src;
    if (!src && audio.querySelector('source[src]:not([src=""])')) {
      src = audio.querySelector('source[src]:not([src=""])').src;
    }

    const enclosures = useEnclosures(window.enclosures || []);
    const matchedAudioEnclosure = enclosures.audios.find(
      item => item.url === src
    );
    const coverEnclosure = enclosures.images.find(item => item.canBeCover);

    audio.dataset.id = matchedAudioEnclosure?.id || audio.src;
    audio.dataset.title =
      matchedAudioEnclosure?.alt ||
      audio.dataset.title ||
      document.querySelector('title')?.textContent ||
      '[No Text]';
    audio.dataset.author =
      document.querySelector('meta[name="author"]')?.getAttribute('content') ||
      '[No Text]';
    audio.dataset.pic = matchedAudioEnclosure?.thumbnail || coverEnclosure?.url;

    const brAudioStatus = window.brAudioStatus;
    const isCurrentAudio = audio.dataset.id === brAudioStatus?.audioId;
    const isPlaying = isCurrentAudio && brAudioStatus?.isPlaying;
    const isLoading = isCurrentAudio && brAudioStatus?.isLoading;

    audio.dataset.playing = isPlaying;
    audio.dataset.loading = isLoading;

    const playerContainer = document.createElement('div');
    playerContainer.className = 'br-audio-container';
    playerContainer.innerHTML = `
      <div class="br-audio-play-button">
        ${
          audio.dataset.pic
            ? `<img src="${audio.dataset.pic}" alt="audio cover" class="br-audio-cover">`
            : ''
        }
        <div class="br-audio-play-icon-container ${isLoading ? 'loading' : ''}">
          ${isLoading ? SVG_LOADING : isPlaying ? SVG_PAUSE : SVG_PLAY}
        </div>
      </div>
      <div class="br-audio-info">
        <div class="br-audio-title">${audio.dataset.title}</div>
        <div class="br-audio-author">${audio.dataset.author}</div>
      </div>
    `;

    audio.parentNode.insertBefore(playerContainer, audio.nextSibling);

    // 添加点击事件
    const playButton = audio.nextElementSibling.querySelector(
      '.br-audio-play-button'
    );
    playButton.addEventListener('click', e => {
      e.stopPropagation();
      if (window.webkit) {
        audio.dataset.playing = !(audio.dataset.playing === 'true');
        const action = audio.dataset.playing === 'true' ? 'play' : 'pause';
        // 发送消息到 SwiftUI
        window.webkit.messageHandlers.audioPlayerHandler.postMessage({
          action: action,
          id: audio.dataset.id,
          src: src,
          title: audio.dataset.title,
          author: audio.dataset.author,
          pic: audio.dataset.pic || '',
        });
      }
    });
  });
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', formatAudio);

// 定义一个全局函数，用于接收 Swift 发送的状态
window.handleAudioStatusUpdate = function (jsonString) {
  let status;
  try {
    status = JSON.parse(jsonString);
  } catch (e) {
    console.log('handleAudioStatusUpdate failed to parse audio status JSON:');
    console.log(e);
    return;
  }

  window.brAudioStatus = status;

  const audios = document.querySelectorAll(
    'audio[src]:not([src=""]), audio:has(source[src]:not([src=""]))'
  );

  audios.forEach(audio => {
    // 获取音频源
    let src = audio.src;
    if (!src && audio.querySelector('source[src]:not([src=""])')) {
      src = audio.querySelector('source[src]:not([src=""])').src;
    }

    const enclosures = useEnclosures(window.enclosures || []);
    const matchedAudioEnclosure = enclosures.audios.find(
      item => item.url === src
    );

    audio.dataset.id = matchedAudioEnclosure?.id || audio.src;

    const isCurrentAudio = audio.dataset.id === status.audioId;
    const isPlaying = isCurrentAudio && status.isPlaying;
    const isLoading = isCurrentAudio && status.isLoading;

    const playIconContainer = audio.nextElementSibling.querySelector(
      '.br-audio-play-icon-container'
    );

    audio.dataset.playing = isPlaying;
    audio.dataset.loading = isLoading;
    playIconContainer.innerHTML = isLoading
      ? SVG_LOADING
      : isPlaying
      ? SVG_PAUSE
      : SVG_PLAY;
  });
};

function useEnclosures(enclosures) {
  const mediaList = {
    images: [],
    videos: [],
    audios: [],
  };

  enclosures.forEach(item => {
    const { mimeType, realMediaType, url } = item;
    if (
      (mimeType.startsWith(MIME_TYPE.image) ||
        realMediaType === MIME_TYPE.image) &&
      url
    ) {
      mediaList.images.push(item);
    } else if (
      (mimeType.startsWith(MIME_TYPE.video) ||
        realMediaType === MIME_TYPE.video) &&
      url
    ) {
      mediaList.videos.push(item);
    } else if (
      (mimeType.startsWith(MIME_TYPE.audio) ||
        realMediaType === MIME_TYPE.audio) &&
      url
    ) {
      mediaList.audios.push(item);
    }
  });

  return mediaList;
}
