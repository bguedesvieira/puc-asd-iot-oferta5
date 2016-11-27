package br.com.monitoramento.integration.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.monitoramento.integration.dto.Channel;
import br.com.monitoramento.integration.dto.Feed;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelResponse{

	private Channel channel;
	private List<Feed> feeds = new ArrayList<Feed>();

	/**
	 * 
	 * @return The channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * 
	 * @param channel
	 *            The channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * 
	 * @return The feeds
	 */
	public List<Feed> getFeeds() {
		return feeds;
	}

	/**
	 * 
	 * @param feeds
	 *            The feeds
	 */
	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}
	
}
