var webpack = require('webpack');
const CleanWebpackPlugin = require('clean-webpack-plugin');
var path = require('path');


module.exports = {
  mode: 'development',
  entry: {
    application: path.resolve(__dirname, './src/js/application.js'),
  },  
  devServer: {
      inline: true,
      contentBase: './src',
      port: 3001,
      proxy: {
        "/": "http://localhost:9292",
        "/landing": "http://localhost:9292",
        "/login": "http://localhost:9292",
        "/logout": "http://localhost:9292"
      }

  },
  module: {
      rules: [        
        {
          use: {
            loader: "babel-loader",
            options: {
              presets: [['@babel/preset-react'], ['@babel/preset-env',{
                "debug": true,
              }]],
            }
          }
        }, 
        {
          test: /\.css$/,
          use: [
            'style-loader',
            'css-loader'
          ]
        },
        {
          test: /\.scss$/,
          use: [
            'style-loader',
            'css-loader',
            'sass-loader'
          ]
        },
        {
          test: /\.(ttf|eot|png|svg|jpg|gif)$/,
           use: [
            'file-loader'
          ]
        },
        { 
          test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/, 
          loader: "url-loader",
          options:{limit:10000,mimetype:"application/font-woff"} 
        },       
      ]
  }, 

  output: {
    publicPath: '/js/',
    filename: '[name]Bundle.js',
  },
};